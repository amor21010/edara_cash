package net.edara.edaracash.features.invoice


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SimpleAdapter
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import net.edara.domain.models.getAllService.GetAllServiceResonse
import net.edara.edaracash.MainActivity
import net.edara.edaracash.R
import net.edara.edaracash.databinding.FragmentInvoiceBinding
import net.edara.edaracash.models.ExtrasDto

const val REQUEST_CODE = 55555

@AndroidEntryPoint
class InvoiceFragment : Fragment() {


    private val viewModel: InvoiceViewModel by viewModels()
    private lateinit var binding: FragmentInvoiceBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentInvoiceBinding.inflate(inflater, container, false)
        val serviceList = InvoiceFragmentArgs.fromBundle(requireArguments()).services.toList()
        val extrasDto = InvoiceFragmentArgs.fromBundle(requireArguments()).extras

        viewModel.getUnitInfo(servicesId = serviceList.map { it.id })
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
            if (binding.methode.isVisible) {
                (requireActivity() as MainActivity).requestPayment(total.toFloat()) {
                    printView()
                }
            } else {
                binding.print.text = resources.getString(R.string.cancel)

                binding.methode.visibility = View.VISIBLE
            }

        }
        binding.print.setOnClickListener {
            if (binding.methode.isVisible) {
                binding.print.text = resources.getString(R.string.print_only)

                binding.methode.visibility = View.GONE
            } else {

                printView()
            }
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
        dialog.setMessage("Please Login Again")
        dialog.setPositiveButton("Login") { dialogInterface, i ->
            findNavController().navigate(R.id.loginFragment)
        }
        dialog.setOnDismissListener {
            findNavController().navigate(R.id.loginFragment)
        }
        dialog.show()
    }

    private fun bindUnitInfo(state: ResultState.Success, total: Double) {
        binding.date.text = binding.date.text.toString() + state.unitInfo?.issueDate
        binding.reciept.text = state.unitInfo?.receiptNo
        binding.project.text = state.unitInfo?.projectName
        binding.unitCode.text = state.unitInfo?.analysisCode
        binding.unitNo.text = state.unitInfo?.unitNumber
        binding.collection.text = state.unitInfo?.collectionNo
        binding.user.text = state.unitInfo?.loggedInUser

        binding.geidea.isEnabled = (total >= 2.0)

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


