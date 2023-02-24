package net.edara.data.remote

import net.edara.domain.models.getAllService.GetAllServiceResonse
import net.edara.domain.models.getAllService.GetAllServicesRequestDto
import net.edara.domain.models.login.LoginRequestDto
import net.edara.domain.models.login.LoginResponse
import net.edara.domain.models.print.PrintRequest
import net.edara.domain.models.print.PrintResponse
import retrofit2.http.Body
import retrofit2.http.Header

import retrofit2.http.POST

interface ApiService {
    @POST("Auth/Login")
    suspend fun login(@Body requestDto: LoginRequestDto): LoginResponse

    @POST("Request/GetAll")
    suspend fun getAllServices(
        @Body requestDto: GetAllServicesRequestDto,
        @Header("authorization") auth: String
    ): GetAllServiceResonse
    @POST("Request/Print")
    suspend fun printServices(
        @Body requestDto: PrintRequest,
        @Header("authorization") auth: String
    ): PrintResponse

}