package net.edara.edaracash.features

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import net.edara.edaracash.databinding.FragmentFawryAuthBinding

@AndroidEntryPoint
class FawryAuthFragment : DialogFragment() {


    private val viewModel: FawryAuthViewModel by viewModels()
    private val binding: FragmentFawryAuthBinding by lazy {
        FragmentFawryAuthBinding.inflate(layoutInflater)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(binding.root)
        dialog.window?.decorView?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)
        binding.loginButton.setOnClickListener {
            val userName = binding.userName.editText?.text.toString()
            val password = binding.password.editText?.text.toString()
            if (userName.isEmpty() || userName == "null") {
                binding.userName.error = "this field is required"
                return@setOnClickListener
            }
            if (password.isEmpty() || password == "null") {
                binding.password.error = "this field is required"
                return@setOnClickListener
            }
            viewModel.saveFawryAuth(userName, password)
            findNavController().navigateUp()
        }




        return dialog

    }


}