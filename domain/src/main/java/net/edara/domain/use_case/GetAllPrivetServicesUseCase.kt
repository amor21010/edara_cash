package net.edara.domain.use_case

import net.edara.domain.models.getAllService.GetAllServicesRequestDto
import net.edara.domain.repo.PrivetServicesRepo

class GetAllPrivetServicesUseCase(private val privetServicesRepo: PrivetServicesRepo) {
    suspend operator fun invoke(getAllServicesRequestDto: GetAllServicesRequestDto,authHeader:String) =
        privetServicesRepo.getAllPrivetServices(getAllServicesRequestDto,authHeader)
}