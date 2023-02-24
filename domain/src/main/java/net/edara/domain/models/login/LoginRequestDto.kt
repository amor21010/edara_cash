package net.edara.domain.models.login



import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class LoginRequestDto(
    @SerializedName("username") val username: String?,
    @SerializedName("password") val password: String?
) : Parcelable