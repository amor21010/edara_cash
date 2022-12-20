package net.edara.edaracash.presentation.pay_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.edara.edaracash.databinding.FragmentPaymentBinding
import net.edara.edaracash.presentation.dialogs.showPrintSuccessDialog
import net.edara.edaracash.presentation.edit_work_order.EditWorkOrderFragmentArgs

class PaymentFragment : Fragment() {


    private lateinit var binding: FragmentPaymentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPaymentBinding.inflate(inflater, container, false)

        val workOrder = EditWorkOrderFragmentArgs.fromBundle(requireArguments()).workOrder

        binding.workOrder = workOrder


        binding.print.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                showPrintSuccessDialog()
            }
        }
        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.payButton.setOnClickListener {
            findNavController().navigate(
                PaymentFragmentDirections.actionPaymentFragmentToInvoiceFragment(
                    workOrder
                )
            )
        }
        return binding.root
    }



}