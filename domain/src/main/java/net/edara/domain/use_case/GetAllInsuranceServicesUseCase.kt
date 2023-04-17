package net.edara.domain.use_case

import net.edara.domain.models.getAllService.GetAllServicesRequestDto
import net.edara.domain.repo.InsuranceRepo

class GetAllInsuranceServicesUseCase(private val insuranceRepo: InsuranceRepo) {
    suspend operator fun invoke(
        getAllServicesRequestDto: GetAllServicesRequestDto,
        authHeader: String
    ) =
        insuranceRepo.getAllInsuranceServices(getAllServicesRequestDto, authHeader)
}