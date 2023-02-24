package net.edara.data.repo

import net.edara.data.remote.ApiService
import net.edara.domain.models.login.LoginRequestDto
import net.edara.domain.models.login.LoginResponse
import net.edara.domain.repo.LoginRepo

class LoginRepoImp(private val apiService: ApiService) : LoginRepo {
    override suspend fun login(userName: String, password: String)
            : LoginResponse =
        apiService.login(
            LoginRequestDto(userName, password)
        )
}