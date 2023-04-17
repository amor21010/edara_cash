package net.edara.domain.use_case

import net.edara.domain.models.payment.PaymentRequest
import net.edara.domain.repo.InsuranceRepo

class InsuranceServicePaymentUseCase(private val insuranceRepo: InsuranceRepo) {
    suspend operator fun invoke(paymentRequest: PaymentRequest, token: String) =
        insuranceRepo.payInsuranceServices(paymentRequest, "Bearer $token")

}