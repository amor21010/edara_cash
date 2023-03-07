package net.edara.edaracash.features.methodes_fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import net.edara.domain.models.payment.PaymentRequest
import net.edara.edaracash.MainActivity
import net.edara.edaracash.R
import net.edara.edaracash.databinding.FragmentMethodBinding
import net.edara.edaracash.features.dialogs.PaymentProssessDialogFragment
import net.edara.edaracash.features.invoice.InvoiceViewModel
import net.edara.edaracash.features.invoice.ResultState

@AndroidEntryPoint
class PaymentMethodFragment : Fragment() {
    lateinit var paymentRequest: PaymentRequest
    lateinit var binding: FragmentMethodBinding
    private val viewModel: PaymentViewModel by viewModels()
    private val printViewModel: InvoiceViewModel by activityViewModels()
    private val dialog = PaymentProssessDialogFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMethodBinding.inflate(layoutInflater, container, false)
        paymentRequest = PaymentMethodFragmentArgs.fromBundle(requireArguments()).paymentRequest
        binding.cash.setOnClickListener {

            pay(1)
        }
        binding.cheque.setOnClickListener {
            pay(2)
        }
        binding.visa.setOnClickListener {
            pay(3)
        }
        binding.fawry.setOnClickListener {
            pay(5)

        }
        binding.transfer.setOnClickListener {
            pay(6)

        }
        binding.cL.setOnClickListener {
            pay(6)

        }
        binding.insurance.setOnClickListener {
            pay(8)

        }
        binding.geidea.setOnClickListener {
            pay(9)

        }
        binding.other.setOnClickListener {
            pay(4)

        }

        viewModel.paymentState.asLiveData().observe(viewLifecycleOwner) { paymentState ->
            when (paymentState) {
                is PaymentViewModel.PaymentState.Failed -> {
                    dismissDialog()
                    Toast.makeText(
                        requireContext(), "Request Failed ${paymentState.msg}", Toast.LENGTH_LONG
                    ).show()
                }
                PaymentViewModel.PaymentState.Init -> {

                }
                PaymentViewModel.PaymentState.Loading -> {
                    showDialog()
                }
                PaymentViewModel.PaymentState.Succeeded -> {
                    dismissDialog()
                    findNavController().navigate(R.id.invoiceFragment)
                }
                PaymentViewModel.PaymentState.Unauthorized -> {
                    dismissDialog()
                    showUnauthorizedDialog()
                }
            }
        }
        return binding.root
    }

    private fun showUnauthorizedDialog() {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle("Session Expired")
        dialog.setMessage("Please Login Again")
        dialog.setCancelable(false)
        dialog.setPositiveButton("Login") { _, _ ->
            findNavController().navigate(R.id.loginFragment)
        }

        dialog.setOnDismissListener {
            findNavController().navigate(R.id.loginFragment)
        }

        dialog.show()
    }

    private fun dismissDialog() {
        dialog.dismiss()
    }

    private fun showDialog() {
        dialog.show(requireActivity().supportFragmentManager, "loading")
    }

    private fun pay(methodID: Int) {
        printViewModel.unitInfo.asLiveData().observe(viewLifecycleOwner) { resultState ->

            if (resultState is ResultState.Success) {

                when (methodID) {
                    9 -> {
                        val total = resultState.unitInfo?.grandTotal?.toFloat() ?: 0f

                        (requireActivity() as MainActivity).requestPayment(total) { transitionNo ->
                            payToTheApi(methodID, transitionNo)
                        }
                    }
                    else -> {
                        payToTheApi(methodID)
                    }
                }
            }
        }


    }

    private fun payToTheApi(methodID: Int, transitionNo: String = "") {
        paymentRequest =
            paymentRequest.copy(paymentMethodId = methodID, transactionNo = transitionNo)
        viewModel.makePayment(paymentRequest)

    }


}