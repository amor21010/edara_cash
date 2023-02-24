package net.edara.domain.repo

import net.edara.domain.models.login.LoginResponse

interface LoginRepo {
suspend fun login(userName:String,password:String): LoginResponse
}