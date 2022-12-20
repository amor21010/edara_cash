package net.edara.edaracash.presentation.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.edara.edaracash.databinding.FragmentLoginBinding
import net.edara.edaracash.presentation.dialogs.LoadingDialogFragment


class LoginFragment : Fragment() {


    private var isLoading: Boolean = false
    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.loginButton.setOnClickListener {
            isLoading = !isLoading
            showLoading()
        }
        return binding.root
    }

    private fun showLoading() {
        if (isLoading) {
            val dialog = LoadingDialogFragment()
         dialog.show(requireActivity().supportFragmentManager,"loading")
            CoroutineScope(Dispatchers.Main).launch {
                delay(3000).apply {
                   dialog.dismiss()
                    findNavController().navigate(
                       LoginFragmentDirections.actionLoginFragmentToChooseOrderTypeFragment()
                    )
                }
            }
        }


    }


}