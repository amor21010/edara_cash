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
import net.edara.domain.models.getAllService.GetAllServiceResonse
import net.edara.domain.models.payment.PaymentRequest
import net.edara.edaracash.MainActivity
import net.edara.edaracash.R
import net.edara.edaracash.databinding.FragmentInvoiceBinding


import net.edara.edaracash.models.Consts
import net.edara.edaracash.models.ExtrasDto
import net.edara.sunmiprinterutill.PrinterUtil
import javax.inject.Inject

const val REQUEST_CODE = 55555

@AndroidEntryPoint
class InvoiceFragment : Fragment() {


    private val viewModel: InvoiceViewModel by activityViewModels()
    private lateinit var binding: FragmentInvoiceBinding
    var permissions: List<String?> = listOf()

    @Inject
    lateinit var printerUtil: PrinterUtil
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentInvoiceBinding.inflate(inflater, container, false)
        val serviceList = InvoiceFragmentArgs.fromBundle(requireArguments()).services.toList()
        val extrasDto = InvoiceFragmentArgs.fromBundle(requireArguments()).extras
        lifecycleScope.launch {
            viewModel.getUnitInfo(servicesId = serviceList.map { it.id })
        }


        viewModel.permissions.asLiveData().observe(viewLifecycleOwner) { userTokenPermissions ->
            if (userTokenPermissions != null) {
                permissions = userTokenPermissions
            }
        }

        val total = setExtrasToView(serviceList, extrasDto)
        viewModel.unitInfo.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is ResultState.Error -> {

                }
                ResultState.Init -> {

                }
                ResultState.Loading -> {

                }
                is ResultState.Success -> {
                    bindUnitInfo(state, total = total)
                }
                ResultState.Unauthorized -> {
                    createUnauthorizedAlert()
                }

            }

        }

        setServiceListToView(serviceList)



        binding.payButton.setOnClickListener {
            if (permissions.contains(Consts.PAY_PERMISSION))
                findNavController().navigate(
                    InvoiceFragmentDirections.actionInvoiceFragmentToPaymentMethodFragment(
                        PaymentRequest(
                            discount = extrasDto.discount,
                            extraCharge = extrasDto.extraCharge,
                            tax = extrasDto.tax,
                            paymentMethodId = null,
                            requestIdentifers = serviceList.map { it.id },
                            transactionNo = null
                        )
                    )
                ) else Toast.makeText(
                requireContext(),
                "You Are Not Allowed To Preform This Action",
                Toast.LENGTH_SHORT
            ).show()

        }

        binding.print.setOnClickListener {
            Log.d("TAG", "onCreateView: $permissions")
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

    private fun bindUnitInfo(state: ResultState.Success, total: Double) {
        binding.date.text = "Date :" + state.unitInfo?.issueDate
        binding.reciept.text = state.unitInfo?.receiptNo
        binding.project.text = state.unitInfo?.projectName
        binding.unitCode.text = state.unitInfo?.analysisCode
        binding.unitNo.text = state.unitInfo?.unitNumber
        binding.collection.text = state.unitInfo?.collectionNo
        binding.user.text = state.unitInfo?.loggedInUser

    }

    private fun setExtrasToView(
        serviceList: List<GetAllServiceResonse.Data.Service>, extrasDto: ExtrasDto
    ): Double {
        val total = serviceList.sumOf {
            (it.amount)?.toDouble() ?: 0.0
        } + extrasDto.extraCharge + extrasDto.tax - extrasDto.discount

        binding.total.text = total.toString()
        binding.extraCharge.text = extrasDto.extraCharge.toString()
        binding.discount.text = extrasDto.discount.toString()
        return total
    }

    private fun setServiceListToView(serviceList: List<GetAllServiceResonse.Data.Service>) {
        val adapter = ServiceAdapter()
        adapter.submitList(serviceList)
        binding.services.adapter = adapter
    }

}


