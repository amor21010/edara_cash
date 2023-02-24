package net.edara.domain.use_case

import net.edara.domain.models.print.PrintRequest
import net.edara.domain.repo.ServicesRepo
import retrofit2.http.Header

class PrintUseCase(private val servicesRepo: ServicesRepo) {
    suspend operator fun invoke(ids: List<String?>?, authHeader: String) =
        servicesRepo.printServices(
            PrintRequest(ids), authHeader
        )
}