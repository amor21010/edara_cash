package net.edara.domain.use_case

import net.edara.domain.models.payment.PaymentRequest
import net.edara.domain.repo.ServicesRepo

class PaymentUseCase(private val servicesRepo: ServicesRepo) {
    suspend operator fun invoke(paymentRequest: PaymentRequest, token: String) =
        servicesRepo.payServices(paymentRequest, "Bearer $token")

}