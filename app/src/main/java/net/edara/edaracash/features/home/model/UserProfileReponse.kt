package net.edara.edaracash.features.home.model


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class UserProfileReponse(
    @SerializedName("data") val `data`: Data?,
    @SerializedName("failures") val failures: List<String>?,
    @SerializedName("message") val message: String?,
    @SerializedName("result") val result: Result?
) : Parcelable {
    @Parcelize
    data class Data(
        @SerializedName("email") val email: String?,
        @SerializedName("fullname") val fullname: String?,
        @SerializedName("id") val id: String?
    ) : Parcelable

    @Parcelize
    data class Result(
        @SerializedName("key") val key: Int?, @SerializedName("value") val value: String?
    ) : Parcelable
}