package net.edara.edaracash.features.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import net.edara.edaracash.databinding.FragmentChooseOrderTypeBinding
import net.edara.edaracash.features.util.TokenUtils
import net.edara.edaracash.models.Consts
import net.edara.edaracash.navigateSafely
import javax.inject.Inject

@AndroidEntryPoint
class ChooseOrderTypeFragment : Fragment() {
    @Inject
    lateinit var dataStore: DataStore<Preferences>

    private lateinit var binding: FragmentChooseOrderTypeBinding
    private val isFawry by lazy {
        val buildList = listOf(
            Build.MANUFACTURER.uppercase(),
            Build.BRAND.uppercase(),
            Build.DEVICE.uppercase(),
            Build.MODEL.uppercase(),
            Build.PRODUCT.uppercase()
        )
        Log.d("TAG", "build: $buildList")
        buildList.any { it.contains("FAWRY") }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseOrderTypeBinding.inflate(inflater, container, false)
        val user = ChooseOrderTypeFragmentArgs.fromBundle(requireArguments()).userProfile
        val token = ChooseOrderTypeFragmentArgs.fromBundle(requireArguments()).token
        binding.userName.text = user.fullname
        binding.userMail.text = user.email
        val userToken = TokenUtils.getUserJWT(token)

        Log.d("TAG", "build: $isFawry")

        dataStore.data.asLiveData().observe(viewLifecycleOwner) { data ->
            val username = data[Consts.FAWRY_USERNAME]
            val password = data[Consts.FAWRY_Password]
            if (!userToken.permissions.isNullOrEmpty()) {
                Log.d("TAG", "onCreateView:${userToken.permissions}")
                if (userToken.permissions.contains(Consts.PRIVET_SERVICE_PERMISSION)) {
                    binding.servicesFragment.isEnabled = true
                    binding.servicesFragment.visibility = View.VISIBLE
                    binding.servicesFragment.setOnClickListener {
                        if (isFawry&&(username.isNullOrEmpty() || password.isNullOrEmpty())) {
                           navigateSafely(ChooseOrderTypeFragmentDirections.actionChooseOrderTypeFragmentToFawryAuthFragment())
                            return@setOnClickListener
                        }
                       navigateSafely(ChooseOrderTypeFragmentDirections.actionChooseOrderTypeFragmentToServicesFragment())
                    }
                }
                if (userToken.permissions.contains(Consts.INSURANCE_PERMISSION)) {
                    binding.insuranceFragment.isEnabled = true
                    binding.insuranceFragment.visibility = View.VISIBLE
                    binding.insuranceFragment.setOnClickListener {
                        if (isFawry&&(username.isNullOrEmpty() || password.isNullOrEmpty())) {
                           navigateSafely(ChooseOrderTypeFragmentDirections.actionChooseOrderTypeFragmentToFawryAuthFragment())
                            return@setOnClickListener
                        }
                       navigateSafely(ChooseOrderTypeFragmentDirections.actionChooseOrderTypeFragmentToInsuranceFragment())
                    }
                }
            }

            if (isFawry&&(username.isNullOrEmpty() || password.isNullOrEmpty())) {
               navigateSafely(ChooseOrderTypeFragmentDirections.actionChooseOrderTypeFragmentToFawryAuthFragment())
                return@observe
            }
        }


        return binding.root
    }


}