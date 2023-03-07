package net.edara.edaracash.features.util

import android.util.Base64
import com.google.gson.Gson
import com.google.gson.JsonParser
import net.edara.edaracash.models.UserToken
import java.nio.charset.StandardCharsets


object TokenUtils {
    fun getUserJWT(token: String): UserToken {
        val userData =
            String(Base64.decode(token.split(".")[1], Base64.DEFAULT), StandardCharsets.UTF_8)
        JsonParser.parseString(userData).asJsonObject
        return Gson().fromJson(userData, UserToken::class.java)

    }
}