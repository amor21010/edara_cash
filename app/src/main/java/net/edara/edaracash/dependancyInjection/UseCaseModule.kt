package net.edara.edaracash.dependancyInjection

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.edara.domain.repo.InsuranceRepo
import net.edara.domain.repo.PrivetServicesRepo
import net.edara.domain.repo.UserRepo
import net.edara.domain.use_case.*

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideLogin(userRepo: UserRepo) = LoginUseCase(userRepo)


    @Provides
    fun provideProfile(userRepo: UserRepo) = ProfileUseCase(userRepo)

    @Provides
    fun paymentUseCase(privetServicesRepo: PrivetServicesRepo) =
        PrivetServicePaymentUseCase(privetServicesRepo)

    @Provides
    fun provideServiceUseCase(privetServicesRepo: PrivetServicesRepo) =
        GetAllPrivetServicesUseCase(privetServicesRepo)

    @Provides
    fun providePrintCase(privetServicesRepo: PrivetServicesRepo) =
        PrivetServicePrintUseCase(privetServicesRepo)

    @Provides
    fun insurancePaymentUseCase(insuranceRepo: InsuranceRepo) =
        InsuranceServicePaymentUseCase(insuranceRepo)

    @Provides
    fun provideInsuranceServiceUseCase(insuranceRepo: InsuranceRepo) =
        GetAllInsuranceServicesUseCase(insuranceRepo)

    @Provides
    fun provideInsurancePrintCase(insuranceRepo: InsuranceRepo) =
        InsuranceServicePrintUseCase(insuranceRepo)
}