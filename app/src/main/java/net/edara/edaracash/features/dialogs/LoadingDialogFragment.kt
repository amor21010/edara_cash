package net.edara.edaracash.features.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import net.edara.edaracash.R


class LoadingDialogFragment (): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.NewDialog)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;

            builder.setView(inflater.inflate(R.layout.loading_dialog, null))

            builder.setCancelable(false)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        dialog?.window?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.md_theme_primary_transparent)));

        dialog?.setCanceledOnTouchOutside(false)
    }

}