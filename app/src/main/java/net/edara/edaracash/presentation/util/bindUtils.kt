package net.edara.edaracash.presentation.util

import android.widget.TextView
import androidx.databinding.BindingAdapter

import com.google.android.material.textfield.TextInputEditText
import net.edara.edaracash.models.WorkOrder

@BindingAdapter("doubleText")
fun TextView.setDoubleText(item: Double) {
    item.let {
        text = ("$item L.E")
    }
}

@BindingAdapter("analysesCode")
fun TextView.setAnalysesCode(item: WorkOrder) {
    item.let {
        text = ("Analyses Code :${item.analysisCode}")
    }
}

@BindingAdapter("unitNumber")
fun TextView.setUnitNumber(item: WorkOrder) {
    item.let {
        text = ("Unit No:${item.unitNo}")
    }
}

@BindingAdapter("clientName")
fun TextView.setClientName(item: WorkOrder) {
    item.let {
        text = ("Client Name:${item.clientName}")
    }
}

@BindingAdapter("doubleText")
fun TextInputEditText.setDoubleText(item: Double) {
    if (item != 0.0)
        setText(item.toString())
}
