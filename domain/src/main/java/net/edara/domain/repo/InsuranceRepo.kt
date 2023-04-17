package net.edara.domain.repo

import net.edara.domain.models.getAllService.GetAllServiceResonse
import net.edara.domain.models.getAllService.GetAllServicesRequestDto
import net.edara.domain.models.payment.PaymentRequest
import net.edara.domain.models.payment.PaymentResponse
import net.edara.domain.models.print.PrintRequest
import net.edara.domain.models.print.PrintResponse

interface InsuranceRepo {
    suspend fun getAllInsuranceServices(
        getAllServicesRequest: GetAllServicesRequestDto,
        authHeader: String
    ): GetAllServiceResonse

    suspend fun printInsuranceServices(printRequest: PrintRequest, authHeader: String): PrintResponse
    suspend fun payInsuranceServices(
        request: PaymentRequest,
        authHeader: String
    ): PaymentResponse
}