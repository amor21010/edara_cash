package net.edara.edaracash.features.landing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.edara.edaracash.R
import net.edara.edaracash.models.UserState


@AndroidEntryPoint
class LandingFragment : Fragment() {
    private val viewModel: LandingViewModel by viewModels()
    private var isFirstTime = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        lifecycleScope.launch {
            delay(2000).also {
                getToken()
            }

        }
        return inflater.inflate(R.layout.fragment_landing, container, false)
    }

    private suspend fun getToken() {

        viewModel.userState.collect { userState ->
            when (userState) {

                UserState.Init -> {


                }

                is UserState.Success -> {
                    if (!isFirstTime) {
                        isFirstTime = true
                        findNavController().navigate(
                            LandingFragmentDirections.actionLandingFragmentToChooseOrderTypeFragment(
                                userState.user, userState.token
                            )
                        )
                    }
                }

                else -> {
                    if (!isFirstTime) {

                        isFirstTime = true
                        findNavController().navigate(LandingFragmentDirections.actionLandingFragmentToLoginFragment())
                    }
                }
            }


        }


    }

}