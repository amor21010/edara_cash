package net.edara.edaracash.features.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.edara.edaracash.databinding.FragmentLoginBinding

import net.edara.edaracash.features.dialogs.LoadingDialogFragment
import net.edara.edaracash.models.UserState
import net.edara.edaracash.navigateSafely

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val dialog = LoadingDialogFragment()

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.loginButton.setOnClickListener {
            showLoading()
        }

        lifecycleScope.launch {

            viewModel.loginState.asLiveData().observe(viewLifecycleOwner) { state ->
                when (state) {
                    is UserState.Failure -> {
                        dialog.dismiss()
                        binding.password.error = state.error
                    }
                    is UserState.Success -> {
                        dialog.dismiss()
                       navigateSafely(
                            LoginFragmentDirections.actionLoginFragmentToChooseOrderTypeFragment(
                                state.user,state.token
                            )
                        )
                    }

                    else -> {

                    }
                }


            }
        }
        return binding.root
    }


    private fun showLoading() {
        dialog.show(requireActivity().supportFragmentManager, "loading")
        lifecycleScope.launch {
            login()
        }

    }

    private suspend fun login() {
        val userName = binding.userName.editText?.text.toString()
        val password = binding.password.editText?.text.toString()
        viewModel.login(userName, password)
    }


}
