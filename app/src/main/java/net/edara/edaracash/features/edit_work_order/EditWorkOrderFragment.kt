package net.edara.edaracash.features.edit_work_order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import net.edara.domain.models.getAllService.GetAllServiceResonse
import net.edara.domain.models.getAllService.GetAllServiceResonse.*
import net.edara.domain.models.getAllService.GetAllServiceResonse.Data.*
import net.edara.edaracash.databinding.FragmentEditWorkOrderBinding

class EditWorkOrderFragment : Fragment() {


    private val viewModel: EditWorkOrderViewModel by lazy {
        ViewModelProvider(this)[EditWorkOrderViewModel::class.java]

    }

    private lateinit var binding: FragmentEditWorkOrderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditWorkOrderBinding.inflate(inflater, container, false)
        var workOrder: Service =
            EditWorkOrderFragmentArgs.fromBundle(requireArguments()).service


        binding.services = workOrder
        viewModel.setOrder(workOrder)
        viewModel.resultOrder.observe(viewLifecycleOwner) {
            workOrder = it


        }
        binding.laborCost.editText?.doAfterTextChanged { text ->

            }



        binding.payButton.setOnClickListener {
          /*  findNavController().navigate(
                EditWorkOrderFragmentDirections.actionEditWorkOrderFragmentToInvoiceFragment(
                    arrayOf(workOrder)
                )
            )
            Log.d("onCreateView", "onCreateView: $workOrder")*/

        }

        binding.cancelButton.setOnClickListener {

            findNavController().navigateUp()
        }

        return binding.root
    }

    private fun shouldPayButtonBeEnabled(text: String) =
        (binding.totalPrice.text != "0.0") &&
                (text.toDouble() != 0.0)


}