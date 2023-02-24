package net.edara.domain.models.getAllService


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class GetAllServicesRequestDto(
    @SerializedName("filter")
    val filter: Filter?,
    @SerializedName("paginator")
    val paginator: Paginator?,
    @SerializedName("sorting")
    val sorting: Sorting?
) : Parcelable {
    @Parcelize
    data class Filter(
        @SerializedName("analysisCode")
        val analysisCode: String?,
        @SerializedName("unitNo")
        val unitNo: String?
    ) : Parcelable

    @Parcelize
    data class Paginator(
        @SerializedName("page")
        val page: Int?=0,
        @SerializedName("pageSize")
        val pageSize: Int?=10
    ) : Parcelable

    @Parcelize
    data class Sorting(
        @SerializedName("column")
        val column: String?="",
        @SerializedName("sortingDirection")
        val sortingDirection: Int?=0
    ) : Parcelable
}