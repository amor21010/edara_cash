package net.edara.edaracash.features.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import net.edara.edaracash.databinding.FragmentChooseOrderTypeBinding

class ChooseOrderTypeFragment : Fragment() {

    private lateinit var binding: FragmentChooseOrderTypeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseOrderTypeBinding.inflate(inflater, container, false)

        binding.insuranceFragment.setOnClickListener {
            findNavController().navigate(ChooseOrderTypeFragmentDirections.actionChooseOrderTypeFragmentToInsuranceFragment())
        }
        binding.servicesFragment.setOnClickListener {
            findNavController().navigate(ChooseOrderTypeFragmentDirections.actionChooseOrderTypeFragmentToServicesFragment())
        }
        return binding.root
    }


}