package net.edara.domain.models.login


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class LoginResponse(
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
        @SerializedName("token")
        val token: String?
    ) : Parcelable

    @Parcelize
    data class Result(
        @SerializedName("key")
        val key: Int?,
        @SerializedName("value")
        val value: String?
    ) : Parcelable

    @Parcelize
    data class Failures(
        @SerializedName("failures")
        val failures: Failures?
    ) : Parcelable {
        @Parcelize
        data class Failures(
            @SerializedName("password")
            val password: List<String?>?,
            @SerializedName("username")
            val username: List<String?>?
        ) : Parcelable
    }
}