package net.edara.domain.use_case

import net.edara.domain.models.print.PrintRequest
import net.edara.domain.repo.PrivetServicesRepo

class PrivetServicePrintUseCase(private val privetServicesRepo: PrivetServicesRepo) {
    suspend operator fun invoke(ids: List<String?>?, authHeader: String) =
        privetServicesRepo.printPrivetServices(
            PrintRequest(ids), authHeader
        )
}