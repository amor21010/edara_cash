package net.edara.domain.repo

import net.edara.domain.models.ProfileResponse
import net.edara.domain.models.login.LoginResponse

interface UserRepo {
    suspend fun login(userName: String, password: String): LoginResponse
    suspend fun getUserProfile(authHeader: String): ProfileResponse
}