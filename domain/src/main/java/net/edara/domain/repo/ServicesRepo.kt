package net.edara.domain.repo

import net.edara.domain.models.getAllService.GetAllServiceResonse
import net.edara.domain.models.getAllService.GetAllServicesRequestDto
import net.edara.domain.models.payment.PaymentRequest
import net.edara.domain.models.payment.PaymentResponse
import net.edara.domain.models.print.PrintRequest
import net.edara.domain.models.print.PrintResponse

interface ServicesRepo {
    suspend fun getAllServices(
        getAllServicesRequest: GetAllServicesRequestDto,
        authHeader: String
    ): GetAllServiceResonse

    suspend fun printServices(printRequest: PrintRequest, authHeader: String): PrintResponse
    suspend fun payServices(
        request: PaymentRequest,
        authHeader: String
    ): PaymentResponse
}