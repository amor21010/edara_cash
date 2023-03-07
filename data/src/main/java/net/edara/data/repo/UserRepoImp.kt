package net.edara.data.repo

import net.edara.data.remote.ApiService
import net.edara.domain.models.ProfileResponse
import net.edara.domain.models.login.LoginRequestDto
import net.edara.domain.models.login.LoginResponse
import net.edara.domain.repo.UserRepo

class UserRepoImp(private val apiService: ApiService) : UserRepo {
    override suspend fun login(userName: String, password: String)
            : LoginResponse =
        apiService.login(
            LoginRequestDto(userName, password)
        )

    override suspend fun getUserProfile(
        authHeader: String
    ): ProfileResponse =
        apiService.getUserProfile(authHeader)

}