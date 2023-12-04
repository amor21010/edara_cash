package net.edara.edaracash.features.methodes_fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.edara.domain.models.payment.PaymentRequest
import net.edara.domain.models.print.PrintResponse
import net.edara.edaracash.MainActivity
import net.edara.edaracash.R
import net.edara.edaracash.databinding.FragmentMethodBinding
import net.edara.edaracash.features.dialogs.PaymentProssessDialogFragment
import net.edara.edaracash.features.util.CommonUtils.isFawryPOS
import net.edara.edaracash.navigateSafely

@AndroidEntryPoint
class PaymentMethodFragment : Fragment() {
    private lateinit var paymentRequest: PaymentRequest
    lateinit var binding: FragmentMethodBinding
    private val viewModel: PaymentViewModel by viewModels()
    private val dialog = PaymentProssessDialogFragment()
    lateinit var invoice: PrintResponse.Data
    private var isInsurance = false
    override fun onAttach(context: Context) {
        super.onAttach(context)
        paymentRequest = PaymentMethodFragmentArgs.fromBundle(requireArguments()).paymentRequest
    }

    override fun onStart() {
        super.onStart()
        paymentRequest = PaymentMethodFragmentArgs.fromBundle(requireArguments()).paymentRequest
    }

    override fun onResume() {
        super.onResume()
        paymentRequest = PaymentMethodFragmentArgs.fromBundle(requireArguments()).paymentRequest
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMethodBinding.inflate(layoutInflater, container, false)
        paymentRequest = PaymentMethodFragmentArgs.fromBundle(requireArguments()).paymentRequest
        isInsurance = PaymentMethodFragmentArgs.fromBundle(requireArguments()).isInsurance
        lifecycleScope.launch {
            viewModel.getUnitInfo(paymentRequest.requestIdentifers!!, isInsurance = isInsurance)
        }

        binding.cash.setOnClickListener {
            pay(1)
        }
        binding.cheque.setOnClickListener {
            pay(2)
        }
        binding.visa.setOnClickListener {
            pay(3)
        }
        if (!isFawryPOS()) {
            binding.fawryLogo.setImageResource(R.drawable.fawry_unactive)

            binding.fawry.setBackgroundColor(
                resources.getColor(
                    R.color.md_theme_light_inverseOnSurface,
                    null
                )
            )
            binding.fawry.elevation = 2f

        } else {
            binding.fawry.elevation = 4f
            binding.fawryLogo.setImageResource(R.drawable.fawry)

            binding.fawry.setOnClickListener {
                pay(5)

            }
        }
        binding.transfer.setOnClickListener {
            pay(6)

        }
        binding.cL.setOnClickListener {
            pay(7)

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

                PaymentViewModel.PaymentState.Loading -> {
                    showDialog()
                }

                PaymentViewModel.PaymentState.Unauthorized -> {
                    dismissDialog()
                    showUnauthorizedDialog()
                }
                else -> {}
            }
        }

        viewModel.unitInfo.asLiveData().observe(viewLifecycleOwner) { resultState ->
            when (resultState) {
                is ResultState.Success -> {
                    binding.layout.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    invoice = resultState.unitInfo!!
                }
                is ResultState.Updated -> {
                    binding.layout.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    invoice = resultState.unitInfo!!
                    dialog.changeView()
                    lifecycleScope.launch {
                        delay(3000).apply {
                            dismiss()
                        }
                    }
                }
                ResultState.Unauthorized -> showUnauthorizedDialog()
                is ResultState.Error -> {
                    Toast.makeText(
                        requireContext(), "Request Failed ${resultState.msg}", Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    binding.layout.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
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
           navigateSafely(R.id.loginFragment)
        }

        dialog.setOnDismissListener {
           navigateSafely(R.id.loginFragment)
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
        val total = invoice.grandTotal.toString().toFloat() + paymentRequest.extraCharge.toString()
            .toFloat() + paymentRequest.tax.toString()
            .toFloat() - paymentRequest.discount.toString().toFloat()

        when (methodID) {

            9 -> {
                (requireActivity() as MainActivity).requestPayment(total) { transitionNo ->
                    payToTheApi(methodID, transitionNo)
                }
            }
            5 -> {

                payWithFawry(
                    total,
                    ({ fawryTransactionNumber ->
                        payToTheApi(methodID, fawryTransactionNumber)
                    })
                )

            }

            else -> {
                payToTheApi(methodID)
            }
        }
    }

    private fun payWithFawry(
        total: Float, onSuccess: (fawryRefrance: String) -> Unit
    ) {

        if (isFawryPOS())
            (requireActivity() as MainActivity).fawryPay(
                amount = total.toDouble(),
                onSuccess = { fawryReference ->
                    onSuccess(fawryReference)
                })
        else Toast.makeText(requireContext(), "this is not fawry pos", Toast.LENGTH_SHORT).show()
    }


    private fun payToTheApi(methodID: Int, transitionNo: String? = null) {
        paymentRequest =
            paymentRequest.copy(paymentMethodId = methodID, transactionNo = transitionNo)
        viewModel.makePayment(paymentRequest, isInsurance)

    }

    private fun dismiss() {
        dismissDialog()
        findNavController().navigateUp()
    }


}