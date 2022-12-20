package net.edara.edaracash.presentation.insurance_search

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import net.edara.edaracash.databinding.FragmentInsuranceBinding

class InsuranceFragment : Fragment() {

    private val viewModel: InsuranceViewModel by lazy {
        ViewModelProvider(this)[InsuranceViewModel::class.java]
    }
    private lateinit var binding: FragmentInsuranceBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInsuranceBinding.inflate(inflater, container, false)
        binding.searchButton.setOnClickListener {
            findNavController().navigate(InsuranceFragmentDirections.actionInsuranceFragmentToResultFragment())
        }
        return binding.root
    }

}