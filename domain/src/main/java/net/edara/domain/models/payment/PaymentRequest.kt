package net.edara.domain.models.payment


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class PaymentRequest(
    @SerializedName("discount")
    val discount: Double?,
    @SerializedName("extraCharge")
    val extraCharge: Double?,
    @SerializedName("paymentMethodId")
    val paymentMethodId: Int?,
    @SerializedName("requestIdentifers")
    val requestIdentifers: List<String?>?,
    @SerializedName("tax")
    val tax: Double?,
    @SerializedName("transactionNo")
    val transactionNo: String?
) : Parcelable