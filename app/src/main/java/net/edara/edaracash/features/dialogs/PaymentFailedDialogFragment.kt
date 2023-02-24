package net.edara.edaracash.features.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import net.edara.edaracash.R


class PaymentFailedDialogFragment (): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.NewDialog)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;

            builder.setView(inflater.inflate(R.layout.payment_failed_dialog, null))

            builder.setCancelable(false)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
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