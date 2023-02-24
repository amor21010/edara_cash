package net.edara.domain.use_case

import net.edara.domain.models.getAllService.GetAllServicesRequestDto
import net.edara.domain.repo.ServicesRepo

class GetAllServicesUseCase(private val servicesRepo: ServicesRepo) {
    suspend operator fun invoke(getAllServicesRequestDto: GetAllServicesRequestDto,authHeader:String) =
        servicesRepo.getAllServices(getAllServicesRequestDto,authHeader)
}