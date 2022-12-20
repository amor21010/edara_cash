package net.edara.edaracash.presentation.service_search

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import net.edara.edaracash.R
import net.edara.edaracash.databinding.FragmentServicesBinding

class ServicesFragment : Fragment() {

    companion object {
        fun newInstance() = ServicesFragment()
    }

    private val viewModel: ServicesViewModel by  lazy {
        ViewModelProvider(this)[ServicesViewModel::class.java]
    }
    private lateinit var binding: FragmentServicesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServicesBinding.inflate(inflater, container, false)

        binding.searchButton.setOnClickListener {
            findNavController().navigate(ServicesFragmentDirections.actionServicesFragmentToResultFragment())
        }
        return binding.root
    }

}