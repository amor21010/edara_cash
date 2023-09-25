package net.edara.data.remote

import net.edara.domain.models.ProfileResponse
import net.edara.domain.models.getAllService.GetAllServiceResonse
import net.edara.domain.models.getAllService.GetAllServicesRequestDto
import net.edara.domain.models.login.LoginRequestDto
import net.edara.domain.models.login.LoginResponse
import net.edara.domain.models.payment.PaymentRequest
import net.edara.domain.models.payment.PaymentResponse
import net.edara.domain.models.print.PrintRequest
import net.edara.domain.models.print.PrintResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header

import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {
    @POST("Auth/Login")
    suspend fun login(@Body requestDto: LoginRequestDto): LoginResponse


    @GET("Auth/GetProfile")
    suspend fun getUserProfile(
        @Header("authorization") auth: String
    ): ProfileResponse

    @PUT("PrivateServiceRequest/PayAll")
    suspend fun privateServicePayAll(
        @Body paymentRequest: PaymentRequest,
        @Header("authorization") auth: String
    ): PaymentResponse
    @POST("PrivateServiceRequest/GetAll")
    suspend fun privateServiceGetAllServices(
        @Body requestDto: GetAllServicesRequestDto,
        @Header("authorization") auth: String
    ): GetAllServiceResonse

    @POST("PrivateServiceRequest/Print")
    suspend fun privateServicePrintServices(
        @Body requestDto: PrintRequest,
        @Header("authorization") auth: String
    ): PrintResponse
    @PUT("InsuranceRequest/PayAll")
    suspend fun insuranceServicePayAll(
        @Body paymentRequest: PaymentRequest,
        @Header("authorization") auth: String
    ): PaymentResponse
    @POST("InsuranceRequest/GetAll")
    suspend fun insuranceServicGetAllServices(
        @Body requestDto: GetAllServicesRequestDto,
        @Header("authorization") auth: String
    ): GetAllServiceResonse

    @POST("InsuranceRequest/Print")
    suspend fun insuranceServicPrintServices(
        @Body requestDto: PrintRequest,
        @Header("authorization") auth: String
    ): PrintResponse

}