package net.edara.edaracash.models


import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import org.json.JSONObject



@Parcelize
data class UserJwt(
    val Name: String?,
    val Role: String?,
    val Sub: String?,
    val TokenValidity: String?,
    val Type: String?,
    val Username: String?,
    val aud: String?,
    val exp: Int?,
    val iat: Int?,
    val iss: String?,
    val nbf: Int?
) : Parcelable