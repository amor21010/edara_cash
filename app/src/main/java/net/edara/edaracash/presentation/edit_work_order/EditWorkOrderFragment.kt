package net.edara.edaracash.presentation.edit_work_order

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.edara.edaracash.databinding.FragmentEditWorkOrderBinding
import net.edara.edaracash.models.WorkOrder
import net.edara.edaracash.presentation.dialogs.SuccessDialogFragment
import net.edara.edaracash.presentation.search_result.ResultViewModel

class EditWorkOrderFragment : Fragment() {


    private val viewModel: ResultViewModel by activityViewModels()

    private lateinit var binding: FragmentEditWorkOrderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditWorkOrderBinding.inflate(inflater, container, false)
        var workOrder: WorkOrder? = null


        viewModel.resultOrder.observe(viewLifecycleOwner) {
            workOrder = it
            binding.workOrder = workOrder

        }

        binding.laborCost.editText?.doAfterTextChanged { text ->

            val cost = if (text.isNullOrBlank()) 0.0 else text.toString().toDouble()
            viewModel.changeLaborCost(cost)
            binding.totalPrice.text = workOrder?.total.toString()
            binding.payButton.isEnabled =
                shouldPayButtonBeEnabled(binding.laborCost.editText!!.text.toString())

        }
        binding.spareParts.editText?.doAfterTextChanged { text ->
            val cost = if (text.isNullOrBlank()) 0.0 else text.toString().toDouble()
            viewModel.changeSparParts(cost)
            binding.totalPrice.text = (workOrder?.total).toString()
        }

        binding.tax.editText?.doAfterTextChanged { text ->
            val cost = if (text.isNullOrBlank()) 0.0 else text.toString().toDouble()
            viewModel.changeTax(cost)
            binding.totalPrice.text = (workOrder?.total).toString()
        }
        binding.extraCharge.editText?.doAfterTextChanged { text ->
            val cost = if (text.isNullOrBlank()) 0.0 else text.toString().toDouble()
            viewModel.changeExtraCharge(cost)
            binding.totalPrice.text = (workOrder?.total).toString()
        }
        binding.discount.editText?.doAfterTextChanged { text ->
            val cost = if (text.isNullOrBlank()) 0.0 else text.toString().toDouble()
            viewModel.changeDiscount(cost)
            binding.totalPrice.text = (workOrder?.total).toString()
        }

        binding.saveChanges.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val dialog = SuccessDialogFragment()
                dialog.show(requireActivity().supportFragmentManager, "edit")
                delay(7000).apply {
                    dialog.dismiss()
                    findNavController().navigateUp()
                }
            }

        }

        binding.payButton.setOnClickListener {
            viewModel.saveChanges()
            Log.d("onCreateView", "onCreateView: $workOrder")
            findNavController().navigate(
                EditWorkOrderFragmentDirections.actionEditWorkOrderFragmentToInvoiceFragment(
                    workOrder!!
                )
            )
        }

        binding.cancelButton.setOnClickListener {
            viewModel.cancelChanges()
            findNavController().navigateUp()
        }

        return binding.root
    }

    private fun shouldPayButtonBeEnabled(text: String) =
        (binding.totalPrice.text != "0.0") &&
                (text.toDouble() != 0.0)


}