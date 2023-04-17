package net.edara.domain.use_case

import net.edara.domain.models.print.PrintRequest
import net.edara.domain.repo.InsuranceRepo

class InsuranceServicePrintUseCase(private val insuranceRepo: InsuranceRepo) {
    suspend operator fun invoke(ids: List<String?>?, authHeader: String) =
        insuranceRepo.printInsuranceServices(
            PrintRequest(ids), authHeader
        )
}