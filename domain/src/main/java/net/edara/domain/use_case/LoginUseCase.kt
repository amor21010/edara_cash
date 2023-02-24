package net.edara.domain.use_case

import net.edara.domain.repo.LoginRepo


class LoginUseCase(private val loginRepo: LoginRepo) {
    suspend operator fun invoke(username: String, password: String) =
        loginRepo.login(username, password)
}