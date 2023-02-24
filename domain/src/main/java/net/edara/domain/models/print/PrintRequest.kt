package net.edara.domain.models.print


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class PrintRequest(
    @SerializedName("requestIdentifers")
    val requestIdentifiers: List<String?>?
) : Parcelable