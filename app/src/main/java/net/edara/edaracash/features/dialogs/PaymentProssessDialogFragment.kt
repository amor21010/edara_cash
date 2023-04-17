package net.edara.edaracash.features.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import net.edara.edaracash.R
import net.edara.edaracash.databinding.PaymentSuccessDialogBinding


class PaymentProssessDialogFragment() : DialogFragment() {
    private lateinit var binder: PaymentSuccessDialogBinding
    fun changeView() {
        binder.success.visibility = View.GONE
        binder.sucess.visibility = View.VISIBLE
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater;
        binder = PaymentSuccessDialogBinding.inflate(inflater)
        val builder = AlertDialog.Builder(requireActivity(), R.style.NewDialog)
        // Get the layout inflater
        builder.setView(binder.root)
        builder.setCancelable(false)
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

