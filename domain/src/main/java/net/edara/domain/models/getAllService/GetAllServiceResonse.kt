package net.edara.domain.models.getAllService


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class GetAllServiceResonse(
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
        @SerializedName("dataList")
        val services: List<Service?>?,
        @SerializedName("totalCount")
        val totalCount: Int?
    ) : Parcelable {
        @Parcelize
        data class Service(
            @SerializedName("amount")
            val amount: String?,
            @SerializedName("id")
            val id: String?,
            @SerializedName("serviceName")
            val serviceName: String?,
            var isSelectedForPayment: Boolean
        ) : Parcelable
    }

    @Parcelize
    data class Result(
        @SerializedName("key")
        val key: Int?,
        @SerializedName("value")
        val value: String?,

        ) : Parcelable

    @Parcelize
    data class Failures(
        @SerializedName("filter.AnalysisCode")
        val filterAnalysisCode: List<String?>?,
        @SerializedName("filter.UnitNo")
        val filterUnitNo: List<String?>?,
        @SerializedName("filter.WorkOrderNo")
        val filterWorkOrderNo: List<String?>?
    ) : Parcelable

}