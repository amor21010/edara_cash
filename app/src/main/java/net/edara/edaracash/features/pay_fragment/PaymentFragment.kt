package net.edara.edaracash.features.pay_fragment


import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import net.edara.domain.models.getAllService.GetAllServiceResonse
import net.edara.domain.models.print.PrintResponse
import net.edara.edaracash.R
import net.edara.edaracash.databinding.FragmentPaymentBinding
import net.edara.edaracash.models.ExtrasDto
import net.edara.edaracash.models.InvoiceBuilder

class PaymentFragment : Fragment() {


    var discount = 0.0
    var tax = 0.0
    var extraCharge = 0.0
    private lateinit var binding: FragmentPaymentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentPaymentBinding.inflate(inflater, container, false)

        val services = PaymentFragmentArgs.fromBundle(requireArguments()).services.toList()
        val unitInfo = PaymentFragmentArgs.fromBundle(requireArguments()).unitInfo


        binding.unitNo.text = unitInfo?.unitNumber
        binding.analysisCode.text = unitInfo?.analysisCode
        binding.clintName.text = unitInfo?.clientName
        services.forEach {
            val textView = TextView(context)
            textView.text = it.serviceName
            textView.textSize = 18f
            textView.setTextColor(resources.getColor(R.color.md_theme_light_onPrimary, null))
            textView.textAlignment = TextView.TEXT_ALIGNMENT_VIEW_END
            textView.setTypeface(null, Typeface.BOLD)
            binding.service.addView(textView)
        }
        var total = services.sumOf {
            (it.amount)?.toDouble() ?: 0.0
        }
        binding.tax.editText?.doAfterTextChanged { text ->
            val cost = try {
                text.toString().toDouble()
            } catch (e: Exception) {
                0.0
            }
            total = totalSum(total, -tax, cost)
            tax = cost
        }
        binding.extraCharge.editText?.doAfterTextChanged { text ->
            val cost = try {
                text.toString().toDouble()
            } catch (e: Exception) {
                0.0
            }
            total = totalSum(total, -extraCharge, cost)
            extraCharge = cost
        }
        binding.discount.editText?.doAfterTextChanged { text ->
            val cost = try {
                text.toString().toDouble()
            } catch (e: Exception) {
                0.0
            }
            total = totalSum(total, +discount, -cost)
            discount = cost
        }
        totalSum(total)

        binding.cancelButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.payButton.setOnClickListener {
            proceedToPayment(services, unitInfo)
        }
        return binding.root
    }

    private fun proceedToPayment(
        services: List<GetAllServiceResonse.Data.Service>,
        unitInfo: PrintResponse.Data?
    ) {
        val extrasDto = ExtrasDto(extraCharge, tax, discount)
        val invoiceBuilder =
            InvoiceBuilder(extrasDto = extrasDto, serviceList = services, unitInfo = unitInfo!!)

        findNavController().navigate(
            PaymentFragmentDirections.actionPaymentFragmentToInvoiceFragment(
                invoiceBuilder
            )
        )
    }


    private fun totalSum(vararg double: Double): Double {
        val sum = double.sum()
        binding.totalPrice.text = sum.toString()
        return sum
    }


}