package net.edara.edaracash.features.invoice


import android.app.AlertDialog
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.edara.domain.models.payment.PaymentRequest
import net.edara.domain.models.print.PrintResponse
import net.edara.edaracash.MainActivity
import net.edara.edaracash.R
import net.edara.edaracash.databinding.FragmentInvoiceBinding
import net.edara.edaracash.models.Consts
import net.edara.edaracash.models.InvoiceBuilder
import net.edara.sunmiprinterutill.GeideaPrinterUtil
import javax.inject.Inject


@AndroidEntryPoint
class InvoiceFragment : Fragment() {
    private val viewModel: InvoiceViewModel by activityViewModels()
    private lateinit var binding: FragmentInvoiceBinding
    private var permissions: List<String?> = listOf()
    lateinit var invoiceBuilder: InvoiceBuilder
    private var isInsurance = false
    private lateinit var invoice: PrintResponse.Data
    private lateinit var serviceAdapter: ServiceAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentInvoiceBinding.inflate(inflater, container, false)
        invoiceBuilder = InvoiceFragmentArgs.fromBundle(requireArguments()).invoice
        Log.d("TAG", "onCreateView: $invoiceBuilder")
        isInsurance = InvoiceFragmentArgs.fromBundle(requireArguments()).isInsurance
        lifecycleScope.launch {
            viewModel.getUnitInfo(
                servicesId = invoiceBuilder.serviceList.map { it.id },
                isInsurance
            )
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.removeInvoice()
                }
            })

        viewModel.permissions.asLiveData().observe(viewLifecycleOwner) { userTokenPermissions ->
            if (userTokenPermissions != null) {
                permissions = userTokenPermissions
            }
        }


        serviceAdapter = ServiceAdapter()
        binding.services.adapter = serviceAdapter

        viewModel.unitInfo.asLiveData().observe(viewLifecycleOwner) { state ->
            Log.d("TAG", "onCreateView: $state")
            when (state) {
                is ResultState.Success -> {
                    invoice = state.unitInfo!!
                    bindUnitInfo(invoice)
                    showHideButtons()
                }

                ResultState.Unauthorized -> {
                    createUnauthorizedAlert()
                }

                is ResultState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Log.d("TAG", "onCreateView: ${state.msg}")

                    val dialog = AlertDialog.Builder(requireContext())
                    dialog.setMessage(state.msg)
                    dialog.setCancelable(true)
                    dialog.setOnDismissListener {
                        findNavController().navigateUp()
                    }
                    dialog.setOnCancelListener {
                        findNavController().navigateUp()
                    }


                    dialog.setPositiveButton(
                        "ok"
                    ) { _, _ -> findNavController().navigateUp() }


                }

                is ResultState.Loading -> binding.progressBar.visibility = View.VISIBLE
                else -> {
                    showHideButtons()

                }
            }

        }






        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }


        return binding.root
    }

    private fun printView() {
        val view = binding.linearLayout
        (activity as MainActivity).printReceipt(view)
        view.requestLayout()
        view.invalidate()

    }


    private fun createUnauthorizedAlert() {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle("Session Expired")
        dialog.setCancelable(false)

        dialog.setMessage("Please Login Again")
        dialog.setPositiveButton("Login") { _, _ ->
            findNavController().navigate(R.id.loginFragment)
        }
        dialog.setOnDismissListener {
            findNavController().navigate(R.id.loginFragment)
        }
        dialog.show()
    }


    private fun bindUnitInfo(invoice: PrintResponse.Data) {
        serviceAdapter.submitList(invoiceBuilder.serviceList)

        binding.progressBar.visibility = View.GONE
        binding.linearLayout.visibility = View.VISIBLE
        binding.date.text = "Date :" + invoice.issueDate
        binding.reciept.text = invoice.receiptNo
        binding.project.text = invoice.projectName
        binding.unitCode.text = invoice.analysisCode
        binding.unitNo.text = invoice.unitNumber
        binding.collection.text = invoice.collectionNo
        binding.user.text = invoice.loggedInUser

        try {
            val imageByteArray: ByteArray = Base64.decode(
                invoice.qrCodeFileName,
                Base64.DEFAULT
            )
            Log.d("TAG", "bindUnitInfo: array $imageByteArray")
            Glide.with(requireContext()).asBitmap().load(imageByteArray).into(binding.qrCode)
        } catch (e: Exception) {
            Log.d("TAG", "bindUnitInfo: error ${e.message}")
        }

        setExtrasToView()
        binding.radio.visibility = View.VISIBLE
        binding.print.setOnClickListener {
            Log.d("TAG", "onCreateView: $permissions")
            invoice.receiptNo
            val allowedToPrintInsurance =
                isInsurance && permissions.contains(Consts.INSURANCE_PRINT_PERMISSION)
            val allowedToPrintPrivetService =
                !isInsurance && permissions.contains(Consts.PRIVET_SERVICE_PRINT_PERMISSION)
            if (allowedToPrintInsurance || allowedToPrintPrivetService)
                printView()
            else Toast.makeText(
                requireContext(),
                "You Are Not Allowed To Preform This Action",
                Toast.LENGTH_SHORT
            ).show()

        }


        binding.payButton.setOnClickListener {
            val allowedToPayInsurance =
                isInsurance && permissions.contains(Consts.INSURANCE_PAY_PERMISSION)
            val allowedToPayPrivetService =
                !isInsurance && permissions.contains(Consts.PRIVET_SERVICE_PAY_PERMISSION)
            if (allowedToPayInsurance || allowedToPayPrivetService)
                findNavController().navigate(
                    InvoiceFragmentDirections.actionInvoiceFragmentToPaymentMethodFragment(
                        /* paymentRequest = */ PaymentRequest(
                            discount = invoiceBuilder.extrasDto.discount,
                            extraCharge = invoiceBuilder.extrasDto.extraCharge,
                            tax = invoiceBuilder.extrasDto.tax,
                            paymentMethodId = null,
                            requestIdentifers = invoiceBuilder.serviceList.map { it.id },
                            transactionNo = null
                        ), isInsurance
                    )
                ) else Toast.makeText(
                requireContext(),
                "You Are Not Allowed To Preform This Action",
                Toast.LENGTH_SHORT
            ).show()

        }

    }

    private fun setExtrasToView(

    ) {
        val total = (invoice.grandTotal?.toDouble()
            ?: 0.0) + invoiceBuilder.extrasDto.extraCharge + invoiceBuilder.extrasDto.tax - invoiceBuilder.extrasDto.discount
        binding.total.text = total.toString()
        binding.extraCharge.text = invoiceBuilder.extrasDto.extraCharge.toString()
        binding.discount.text = invoiceBuilder.extrasDto.discount.toString()
    }

    private fun showHideButtons() {
        try {

            //The receipt has been paid before
            val receiptNo = (invoice.receiptNo.toString().toDouble())
            if (receiptNo != 0.0) {
                binding.payButton.visibility = View.GONE
                binding.cancelButton.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            //The receipt has not been paid before
            binding.payButton.visibility = View.VISIBLE
            binding.cancelButton.visibility = View.GONE

        }
    }

}


