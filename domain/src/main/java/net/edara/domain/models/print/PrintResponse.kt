package net.edara.domain.models.print


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class PrintResponse(
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("failures")
    val failures: Failures?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("result")
    val result: Result?
) : Parcelable {
    @Parcelize
    data class Data(
        @SerializedName("analysisCode")
        val analysisCode: String?,
        @SerializedName("clientName")
        val clientName: String?,
        @SerializedName("collectionNo")
        val collectionNo: String?,
        @SerializedName("discount")
        val discount: String?,
        @SerializedName("extraCharge")
        val extraCharge: String?,
        @SerializedName("grandTotal")
        val grandTotal: String?,
        @SerializedName("issueDate")
        val issueDate: String?,
        @SerializedName("loggedInUser")
        val loggedInUser: String?,
        @SerializedName("notes")
        val notes: String?,
        @SerializedName("paymentMethod")
        val paymentMethod: String?,
        @SerializedName("projectName")
        val projectName: String?,
        @SerializedName("qrCodeFileName")
        val qrCodeFileName: String?,
        @SerializedName("receiptNo")
        val receiptNo: String?,
        @SerializedName("requests")
        val requests: List<Request?>?,
        @SerializedName("tax")
        val tax: String?,
        @SerializedName("unitNumber")
        val unitNumber: String?
    ) : Parcelable {
        @Parcelize
        data class Request(
            @SerializedName("cost")
            val cost: String?,
            @SerializedName("duration")
            val duration: String?,
            @SerializedName("newspapers")
            val newspapers: String?,
            @SerializedName("quantity")
            val quantity: String?,
            @SerializedName("serviceName")
            val serviceName: String?,
            @SerializedName("unitCars")
            val unitCars: List<UnitCar?>?,
            @SerializedName("unitMeterDetails")
            val unitMeterDetails: UnitMeterDetails?
        ) : Parcelable {
            @Parcelize
            data class UnitCar(
                @SerializedName("brand")
                val brand: String?,
                @SerializedName("color")
                val color: String?,
                @SerializedName("defaultSticker")
                val defaultSticker: String?,
                @SerializedName("model")
                val model: String?,
                @SerializedName("plateNumber")
                val plateNumber: String?
            ) : Parcelable

            @Parcelize
            data class UnitMeterDetails(
                @SerializedName("area")
                val area: String?,
                @SerializedName("meterPrice")
                val meterPrice: String?
            ) : Parcelable
        }
    }

    @Parcelize
    data class Failures(
        @SerializedName("requestIdentifers")
        val requestIdentifiers: List<String?>?
    ) : Parcelable

    @Parcelize
    data class Result(
        @SerializedName("key")
        val key: Int?,
        @SerializedName("value")
        val value: String?
    ) : Parcelable
}