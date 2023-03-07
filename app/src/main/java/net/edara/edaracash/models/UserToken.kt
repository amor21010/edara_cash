package net.edara.edaracash.models


import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


@Parcelize
data class UserToken(
    @SerializedName("aud")
    val aud: String?,
    @SerializedName("exp")
    val exp: Int?,
    @SerializedName("iat")
    val iat: Int?,
    @SerializedName("iss")
    val iss: String?,
    @SerializedName("Name")
    val name: String?,
    @SerializedName("nbf")
    val nbf: Int?,
    @SerializedName("Permissions")
    val permissions: List<String?>?,
    @SerializedName("Role")
    val role: String?,
    @SerializedName("Sub")
    val sub: String?,
    @SerializedName("TokenValidity")
    val tokenValidity: String?,
    @SerializedName("Type")
    val type: String?,
    @SerializedName("Username")
    val username: String?
) : Parcelable