package net.edara.domain.models.payment


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class PaymentResponse(
    @SerializedName("data")
    val `data`: Boolean?,
    @SerializedName("failures")
    val failures: Failures?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("result")
    val result: Result?
) : Parcelable {
    @Parcelize
    data class Result(
        @SerializedName("key")
        val key: Int?,
        @SerializedName("value")
        val value: String?
    ) : Parcelable

    @Parcelize
    data class Failures(
        @SerializedName("requestIdentifers")
        val requestIdentifers: List<String?>?
    ) : Parcelable

}