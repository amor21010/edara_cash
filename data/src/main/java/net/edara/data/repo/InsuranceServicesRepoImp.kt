package net.edara.data.repo

import net.edara.data.remote.ApiService
import net.edara.domain.models.getAllService.GetAllServiceResonse
import net.edara.domain.models.getAllService.GetAllServicesRequestDto
import net.edara.domain.models.payment.PaymentRequest
import net.edara.domain.models.payment.PaymentResponse
import net.edara.domain.models.print.PrintRequest
import net.edara.domain.models.print.PrintResponse
import net.edara.domain.repo.InsuranceRepo

class InsuranceServicesRepoImp(private val apiService: ApiService) : InsuranceRepo {
    override suspend fun getAllInsuranceServices(
        getAllServicesRequest: GetAllServicesRequestDto, authHeader: String
    ): GetAllServiceResonse {
        return apiService.insuranceServicGetAllServices(getAllServicesRequest, authHeader)
    }

    override suspend fun printInsuranceServices(
        printRequest: PrintRequest, authHeader: String
    ): PrintResponse {
        return apiService.insuranceServicPrintServices(printRequest, authHeader)
    }

    override suspend fun payInsuranceServices(
        request: PaymentRequest, authHeader: String
    ): PaymentResponse {
        return apiService.insuranceServicePayAll(request, authHeader)
    }

}