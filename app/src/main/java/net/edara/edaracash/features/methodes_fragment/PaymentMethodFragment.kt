package net.edara.edaracash.features.methodes_fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
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
import net.edara.edaracash.features.dialogs.FawryAuthDialogFragment
import net.edara.edaracash.features.dialogs.PaymentProssessDialogFragment

@AndroidEntryPoint
class PaymentMethodFragment : Fragment() {
    private lateinit var paymentRequest: PaymentRequest
    lateinit var binding: FragmentMethodBinding
    private val viewModel: PaymentViewModel by viewModels()
    private lateinit var fawryAuthDialog: FawryAuthDialogFragment
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
        binding.fawry.setOnClickListener {
            pay(5)

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
                viewModel.getStoredFawryUser().asLiveData()
                    .observe(viewLifecycleOwner) { fawryUser ->
                        if (fawryUser == null) {
                            createFawryDialog(total, "", "")
                        } else {
                            if (!fawryUser.username.isNullOrEmpty() && !fawryUser.password.isNullOrEmpty())
                                createFawryDialog(total, fawryUser.username, fawryUser.password)
                        }

                    }
            }

            else -> {
                payToTheApi(methodID)
            }
        }
    }

    private fun createFawryDialog(total: Float, savedUserName: String, savedPassword: String) {

        if (::fawryAuthDialog.isInitialized) showFawryDialog()
        else {
            fawryAuthDialog =
                FawryAuthDialogFragment(savedUserName, savedPassword) { username, password ->
                    (requireActivity() as MainActivity).fawryPay(
                        name = username,
                        password = password,
                        amount = total.toDouble(),
                        onSuccess = { fawryReference ->
                            payToTheApi(5, fawryReference)
                            viewModel.saveUserData(username, password)
                        })
                    showFawryDialog()
                }
        }

    }

    private fun showFawryDialog() {
        if (!fawryAuthDialog.isAdded && !fawryAuthDialog.isVisible) fawryAuthDialog.show(
            requireActivity().supportFragmentManager, "Fawry Auth"
        )
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