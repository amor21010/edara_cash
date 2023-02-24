package net.edara.edaracash.features.util

import android.util.Log
import com.auth0.android.jwt.JWT
import net.edara.edaracash.models.UserJwt
import org.json.JSONObject


object JWTUtils {
    fun getUserJWT(token: String): UserJwt? {
        val string = (JWT(token))
        try {
            return UserJwt(
                Name = string.getClaim("Name").asString(),
                Role = string.getClaim("Role").asString(),
                Username = string.getClaim("Username").asString(),
                TokenValidity = string.getClaim("TokenValidity").asString(),
                Type = string.getClaim("Type").asString(),
                Sub = string.getClaim("Sub").asString(),
                aud = string.getClaim("aud").asString(),
                iss = string.getClaim("iss").asString(),
                exp = string.getClaim("exp").asInt(),
                iat = string.getClaim("iat").asInt(),
                nbf = string.getClaim("nbf").asInt(),
            )
        } catch (e: Exception) {
            return null
        }
    }
}