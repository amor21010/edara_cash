package net.edara.edaracash.features.landing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.edara.edaracash.R


@AndroidEntryPoint
class LandingFragment : Fragment() {
    private val viewModel: LandingViewModel by viewModels()
    private var isFirstTime = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        CoroutineScope(Dispatchers.Main).launch {
            getToken()
        }
        return inflater.inflate(R.layout.fragment_landing, container, false)
    }

    private suspend fun getToken() {

        viewModel.token.collect { token ->
            delay(2000).apply {

                if (token == "" && !isFirstTime) {
                    isFirstTime = true
                    findNavController().navigate(LandingFragmentDirections.actionLandingFragmentToLoginFragment())
                } else {
                    if (token != null && !isFirstTime) {
                        isFirstTime = true
                        findNavController().navigate(LandingFragmentDirections.actionLandingFragmentToChooseOrderTypeFragment())
                    }
                }
            }


        }


    }

}