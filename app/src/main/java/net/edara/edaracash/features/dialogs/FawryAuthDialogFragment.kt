package net.edara.edaracash.features.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import net.edara.edaracash.R
import net.edara.edaracash.databinding.FawryAuthDialogBinding


class FawryAuthDialogFragment(val onConfirm: (userName: String, Password: String) -> Unit) :
    DialogFragment() {
    private lateinit var binder: FawryAuthDialogBinding


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater;
        binder = FawryAuthDialogBinding.inflate(inflater)
        val builder = AlertDialog.Builder(requireActivity(), R.style.NewDialog)

        builder.setView(binder.root)
        builder.setCancelable(false)
        binder.confirmButton.setOnClickListener {
            val userName = binder.userName.editText?.text.toString()
            val password = binder.password.editText?.text.toString()
            if (userName.isBlank() || userName == "null") {
                binder.userName.error = "User Name Must Be Entered"
                return@setOnClickListener
            }
            if (password.isBlank() || password == "null") {
                binder.password.error = "password Must Be Entered"
                return@setOnClickListener
            }
            onConfirm(userName, password)
        }
        binder.cancelButton.setOnClickListener {
            dismiss()
        }
        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        dialog?.setCanceledOnTouchOutside(false)
    }

}

