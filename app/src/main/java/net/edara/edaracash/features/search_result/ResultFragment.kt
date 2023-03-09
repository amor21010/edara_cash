package net.edara.edaracash.features.search_result

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import net.edara.domain.models.getAllService.GetAllServiceResonse.Data.Service
import net.edara.domain.models.print.PrintResponse
import net.edara.edaracash.R
import net.edara.edaracash.databinding.FragmentResultBinding

@AndroidEntryPoint
class ResultFragment : Fragment() {

    private lateinit var binding: FragmentResultBinding
    private lateinit var adapter: ResultAdapter
    private var unitInfo: PrintResponse.Data? = null
    private val viewModel: ResultViewModel by viewModels()
    private var selectedItems = listOf<Service>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultBinding.inflate(inflater, container, false)
        val services = ResultFragmentArgs.fromBundle(requireArguments()).services


        viewModel.getUnitInfo(servicesId = services[0].id)
        viewModel.unitInfo.asLiveData().observe(viewLifecycleOwner) { resultState ->
            when (resultState) {
                is ResultState.Error -> showError()
                ResultState.Loading -> showLoading()
                is ResultState.Success -> bindUnitInfoToView(resultState)
                ResultState.Unauthorized -> {
                    createUnauthorizedAlert()
                }
                ResultState.Init -> {}
            }
        }



        adapter = createAdapter()
        adapter.submitList(services.toList())
        binding.resultRecycler.isNestedScrollingEnabled = false
        binding.resultRecycler.adapter = adapter



        getSelected(services)
        binding.selectAllCheckBox.setOnCheckedChangeListener { _, isChecked ->
            adapter.toggleSelectAll(isAllSelected = isChecked)
        }

        binding.payButton.setOnClickListener {
            findNavController().navigate(
                ResultFragmentDirections.actionResultFragmentToPaymentFragment(
                    selectedItems.toTypedArray(), unitInfo
                )
            )
        }
        return binding.root
    }

    private fun bindUnitInfoToView(resultState: ResultState.Success) {
        binding.progressBar.visibility = View.GONE
        binding.view.visibility = View.VISIBLE
        unitInfo = resultState.unitInfo
        binding.clintName.text = unitInfo?.clientName
        binding.analysisCode.text = unitInfo?.analysisCode
        binding.unitNo.text = unitInfo?.unitNumber

    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.view.visibility = View.GONE
    }

    private fun showError() {

    }

    private fun getSelected(services: Array<Service>) {
        viewModel.selectedServices.observe(viewLifecycleOwner) { selectedOrders ->
            selectedItems = selectedOrders
            adapter.selectedItems = selectedOrders.toMutableList()
            if (selectedOrders.isNotEmpty()) {
                binding.payButton.visibility = View.VISIBLE
                binding.selectAllCheckBox.isChecked = selectedOrders.size == services.size
            } else {
                binding.payButton.visibility = View.GONE
            }

        }
    }

    private fun createAdapter() = ResultAdapter(itemClick = { item ->
        if (selectedItems.contains(item)) {
            viewModel.removeOrderFromSelection(item)

        } else {
            if (selectedItems.isNotEmpty()) viewModel.addServiceToSelection(services = item)
            else findNavController().navigate(
                ResultFragmentDirections.actionResultFragmentToPaymentFragment(
                    arrayOf(item), unitInfo
                )
            )
        }

    }, onItemSelect = { item ->
        if (item.isSelectedForPayment) viewModel.addServiceToSelection(item)
        else viewModel.removeOrderFromSelection(item)
    })

    private fun createUnauthorizedAlert() {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle("Session Expired")
        dialog.setCancelable(false)

        dialog.setMessage("Please Login Again")
        dialog.setPositiveButton("Login") { dialogInterface, i ->
            findNavController().navigate(R.id.loginFragment)
        }
        dialog.setOnDismissListener {
            findNavController().navigate(R.id.loginFragment)
        }
        dialog.show()
    }


}