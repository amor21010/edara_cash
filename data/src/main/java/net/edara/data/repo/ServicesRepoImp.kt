package net.edara.data.repo

import net.edara.data.remote.ApiService
import net.edara.domain.models.getAllService.GetAllServiceResonse
import net.edara.domain.models.getAllService.GetAllServicesRequestDto
import net.edara.domain.models.print.PrintRequest
import net.edara.domain.models.print.PrintResponse
import net.edara.domain.repo.ServicesRepo

class ServicesRepoImp(private val apiService: ApiService) : ServicesRepo {
    override suspend fun getAllServices(
        getAllServicesRequest: GetAllServicesRequestDto, authHeader: String
    ): GetAllServiceResonse {
        return apiService.getAllServices(getAllServicesRequest, authHeader)
    }

    override suspend fun printServices(
        printRequest: PrintRequest, authHeader: String
    ): PrintResponse {
        return apiService.printServices(printRequest, authHeader)
    }

    override suspend fun payServices(
        getAllServicesRequest: GetAllServicesRequestDto, authHeader: String
    ): GetAllServiceResonse {
        return apiService.getAllServices(getAllServicesRequest, authHeader)
    }

}