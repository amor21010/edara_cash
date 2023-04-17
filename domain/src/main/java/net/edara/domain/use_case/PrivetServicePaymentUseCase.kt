package net.edara.domain.use_case

import net.edara.domain.models.payment.PaymentRequest
import net.edara.domain.repo.PrivetServicesRepo

class PrivetServicePaymentUseCase(private val privetServicesRepo: PrivetServicesRepo) {
    suspend operator fun invoke(paymentRequest: PaymentRequest, token: String) =
        privetServicesRepo.payServices(paymentRequest, "Bearer $token")

}