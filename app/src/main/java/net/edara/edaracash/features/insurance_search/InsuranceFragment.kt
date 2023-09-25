package net.edara.edaracash.features.insurance_search

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.edara.domain.models.getAllService.GetAllServiceResonse
import net.edara.edaracash.R
import net.edara.edaracash.databinding.FragmentInsuranceBinding
import net.edara.edaracash.features.service_search.SearchState
import net.edara.edaracash.navigateSafely

@AndroidEntryPoint
class InsuranceFragment : Fragment() {
    private val viewModel: InsuranceViewModel by viewModels()

    private var servicesList = arrayOf<GetAllServiceResonse.Data.Service?>()
    private val isNavigationDone = false
    private lateinit var binding: FragmentInsuranceBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInsuranceBinding.inflate(inflater, container, false)


        binding.searchButton.setOnClickListener {
            getAllServices()
        }
        lifecycleScope.launch {
            viewModel.service.collect { response ->
                when (response) {
                    is SearchState.Failed -> {
                        binding.searchButton.isEnabled = true

                        val previousText = binding.failuers.text.toString()
                        binding.searchButton.text = "Search"

                        binding.failuers.text =
                            if (previousText.isNotEmpty() && previousText != "null") previousText + "\n" + response.msg
                            else response.msg
                    }
                    SearchState.Loading -> {
                        binding.failuers.text = ""

                        binding.searchButton.isEnabled=false
                        for (i in 1..5) {
                            var text = binding.searchButton.text.toString()
                            if (i == 1 || i == 5) {
                                binding.searchButton.text = "Please Wait."
                            } else {
                                text += "."
                                binding.searchButton.text = text
                            }
                        }
                    }
                    is SearchState.Success -> {
                        binding.searchButton.isEnabled=true

                        binding.failuers.text = ""
                        binding.searchButton.text = "Search"
                        if (!isNavigationDone) {
                            servicesList = response.service.toTypedArray()
                           navigateSafely(
                                InsuranceFragmentDirections.actionInsuranceFragmentToResultFragment(
                                    servicesList, true
                                )
                            )
                            viewModel.resetState()
                        } else viewModel.resetState()
                    }
                    SearchState.UnAuthorized -> {
                        binding.searchButton.isEnabled=true

                        binding.failuers.text = "Session Expired"
                        binding.searchButton.text = "Search"

                        createUnauthorizedAlert()
                    }
                    SearchState.Init -> {
                        binding.searchButton.isEnabled=true

                    }
                }

            }
        }
        return binding.root
    }

    private fun createUnauthorizedAlert() {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle("Session Expired")
        dialog.setCancelable(false)

        dialog.setMessage("Please Login Again")
        dialog.setPositiveButton("Login") { _, _ ->
           navigateSafely(R.id.loginFragment)
        }

        dialog.show()
    }

    private fun getAllServices() {
        val unitNo = binding.unitNo.editText?.text.toString()
        val analysisCode = binding.analysisCode.editText?.text.toString()
        viewModel.getAllService(unitNumber = unitNo, analysisCode = analysisCode)


    }
}