package net.edara.edaracash.features.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import net.edara.edaracash.databinding.FragmentChooseOrderTypeBinding
import net.edara.edaracash.features.util.TokenUtils
import net.edara.edaracash.models.Consts

class ChooseOrderTypeFragment : Fragment() {

    private lateinit var binding: FragmentChooseOrderTypeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseOrderTypeBinding.inflate(inflater, container, false)
        val user = ChooseOrderTypeFragmentArgs.fromBundle(requireArguments()).userProfile
        val token = ChooseOrderTypeFragmentArgs.fromBundle(requireArguments()).token
        binding.userName.text = user.fullname
        binding.userMail.text = user.email
        val userToken = TokenUtils.getUserJWT(token)
        if (userToken != null && !userToken.permissions.isNullOrEmpty()) {
            Log.d("TAG", "onCreateView:${userToken.permissions}")
            if (userToken.permissions.contains(Consts.PRIVET_SERVICE_PERMISSION)) {
                binding.servicesFragment.isEnabled = true
                binding.servicesFragment.visibility = View.VISIBLE
                binding.servicesFragment.setOnClickListener {
                    findNavController().navigate(ChooseOrderTypeFragmentDirections.actionChooseOrderTypeFragmentToServicesFragment())
                }
            }
            if (userToken.permissions.contains(Consts.INSURANCE_PERMISSION)) {
                binding.insuranceFragment.isEnabled = true
                binding.insuranceFragment.visibility = View.VISIBLE
                binding.insuranceFragment.setOnClickListener {
                    findNavController().navigate(ChooseOrderTypeFragmentDirections.actionChooseOrderTypeFragmentToServicesFragment())
                }
            }
        }



        return binding.root
    }


}