package net.edara.data.repo

import net.edara.data.remote.ApiService
import net.edara.domain.models.getAllService.GetAllServiceResonse
import net.edara.domain.models.getAllService.GetAllServicesRequestDto
import net.edara.domain.models.payment.PaymentRequest
import net.edara.domain.models.payment.PaymentResponse
import net.edara.domain.models.print.PrintRequest
import net.edara.domain.models.print.PrintResponse
import net.edara.domain.repo.PrivetServicesRepo

class PrivetServicesRepoImp(private val apiService: ApiService) : PrivetServicesRepo {
    override suspend fun getAllPrivetServices(
        getAllServicesRequest: GetAllServicesRequestDto, authHeader: String
    ): GetAllServiceResonse {
        return apiService.privateServiceGetAllServices(getAllServicesRequest, authHeader)
    }

    override suspend fun printPrivetServices(
        printRequest: PrintRequest, authHeader: String
    ): PrintResponse {
        return apiService.privateServicePrintServices(printRequest, authHeader)
    }

    override suspend fun payServices(
        request: PaymentRequest, authHeader: String
    ): PaymentResponse {
        return apiService.privateServicePayAll(request, authHeader)
    }

}