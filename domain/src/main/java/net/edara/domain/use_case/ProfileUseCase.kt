package net.edara.domain.use_case

import net.edara.domain.repo.UserRepo


class ProfileUseCase(private val userRepo: UserRepo) {
    suspend operator fun invoke(authHeader:String) =
        userRepo.getUserProfile("bearer $authHeader")
}