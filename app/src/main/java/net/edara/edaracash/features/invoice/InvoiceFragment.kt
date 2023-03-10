package net.edara.edaracash.features.invoice


import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.edara.domain.models.payment.PaymentRequest
import net.edara.edaracash.MainActivity
import net.edara.edaracash.R
import net.edara.edaracash.databinding.FragmentInvoiceBinding
import net.edara.edaracash.models.Consts
import net.edara.edaracash.models.InvoiceBuilder
import net.edara.sunmiprinterutill.PrinterUtil
import javax.inject.Inject


@AndroidEntryPoint
class InvoiceFragment : Fragment() {
    private val viewModel: InvoiceViewModel by activityViewModels()
    private lateinit var binding: FragmentInvoiceBinding
    private var permissions: List<String?> = listOf()

    @Inject
    lateinit var printerUtil: PrinterUtil
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentInvoiceBinding.inflate(inflater, container, false)
        var invoice: InvoiceBuilder = InvoiceFragmentArgs.fromBundle(requireArguments()).invoice
        lifecycleScope.launch {
            viewModel.getUnitInfo(servicesId = invoice.serviceList.map { it.id })
        }


        viewModel.permissions.asLiveData().observe(viewLifecycleOwner) { userTokenPermissions ->
            if (userTokenPermissions != null) {
                permissions = userTokenPermissions
            }
        }

        setExtrasToView(invoice)
        viewModel.unitInfo.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is ResultState.Success -> {
                    invoice = invoice.copy(unitInfo = state.unitInfo!!)
                    bindUnitInfo(invoice)
                }
                ResultState.Unauthorized -> {
                    createUnauthorizedAlert()
                }

                else -> {}
            }

        }

        setServiceListToView(invoice)



        binding.payButton.setOnClickListener {
            try {
                Log.d("onCreateView", "onCreateView: ${invoice.unitInfo.receiptNo}")
                val recieptNo = (invoice.unitInfo.receiptNo.toString().toDouble())
                if (recieptNo != 0.0) Toast.makeText(
                    requireContext(), "This Service Has Been Paid Before.", Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                if (permissions.contains(Consts.PAY_PERMISSION)) findNavController().navigate(
                    InvoiceFragmentDirections.actionInvoiceFragmentToPaymentMethodFragment(
                        PaymentRequest(
                            discount = invoice.extrasDto.discount,
                            extraCharge = invoice.extrasDto.extraCharge,
                            tax = invoice.extrasDto.tax,
                            paymentMethodId = null,
                            requestIdentifers = invoice.serviceList.map { it.id },
                            transactionNo = null
                        )
                    )
                ) else Toast.makeText(
                    requireContext(),
                    "You Are Not Allowed To Preform This Action",
                    Toast.LENGTH_SHORT
                ).show()

            }

        }

        binding.print.setOnClickListener {
            Log.d("TAG", "onCreateView: $permissions")
            invoice.unitInfo.receiptNo
            if (permissions.contains(Consts.PRINT_PERMISSION))
                printView()
            else Toast.makeText(
                requireContext(),
                "You Are Not Allowed To Preform This Action",
                Toast.LENGTH_SHORT
            ).show()

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

    private fun bindUnitInfo(invoice: InvoiceBuilder) {
        binding.progressBar.visibility=View.GONE
        binding.linearLayout.visibility=View.VISIBLE
        binding.date.text = "Date :" + invoice.unitInfo.issueDate
        binding.reciept.text = invoice.unitInfo.receiptNo
        binding.project.text = invoice.unitInfo.projectName
        binding.unitCode.text = invoice.unitInfo.analysisCode
        binding.unitNo.text = invoice.unitInfo.unitNumber
        binding.collection.text = invoice.unitInfo.collectionNo
        binding.user.text = invoice.unitInfo.loggedInUser
    }

    private fun setExtrasToView(
        invoiceBuilder: InvoiceBuilder
    ) {
        val total = (invoiceBuilder.unitInfo.grandTotal?.toDouble()
            ?: 0.0) + invoiceBuilder.extrasDto.extraCharge + invoiceBuilder.extrasDto.tax - invoiceBuilder.extrasDto.discount
        binding.total.text = total.toString()
        binding.extraCharge.text = invoiceBuilder.extrasDto.extraCharge.toString()
        binding.discount.text = invoiceBuilder.extrasDto.discount.toString()
    }

    private fun setServiceListToView(invoice: InvoiceBuilder) {
        val adapter = ServiceAdapter()
        adapter.submitList(invoice.serviceList)
        binding.services.adapter = adapter
        bindUnitInfo(invoice)
    }

}


