package net.edara.edaracash.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class WorkOrder(
    val id: String,
    val title: String,
    val clientName: String,
    val unitNo: Int,
    val analysisCode: Int,
    var laborCost: Double,
    var spareParts: Double,
    var tax: Double = 0.0,
    var discount: Double = 0.0,
    var extraCharge: Double = 0.0,
    var total: Double = (spareParts + laborCost + tax + extraCharge - discount)
) : Parcelable {

}