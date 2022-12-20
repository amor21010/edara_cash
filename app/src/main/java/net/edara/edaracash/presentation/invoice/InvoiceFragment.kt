package net.edara.edaracash.presentation.invoice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.edara.edaracash.databinding.FragmentInvoiceBinding
import net.edara.edaracash.presentation.dialogs.showPrintSuccessDialog

class InvoiceFragment : Fragment() {

    companion object {
        fun newInstance() = InvoiceFragment()
    }

    private val viewModel: InvoiceViewModel by lazy {
        ViewModelProvider(this)[InvoiceViewModel::class.java]
    }
    private lateinit var binding: FragmentInvoiceBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInvoiceBinding.inflate(inflater, container, false)
        val workOrder = InvoiceFragmentArgs.fromBundle(requireArguments()).order
        binding.order = workOrder

        binding.payButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                showPrintSuccessDialog()
            }
        }
        binding.print.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                showPrintSuccessDialog()
            }
        }
        return binding.root
    }


}