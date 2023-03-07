package net.edara.domain.use_case

import net.edara.domain.repo.UserRepo


class LoginUseCase(private val userRepo: UserRepo) {
    suspend operator fun invoke(username: String, password: String) =
        userRepo.login(username, password)
}