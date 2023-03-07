package net.edara.edaracash.dependancyInjection

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.edara.domain.repo.ServicesRepo
import net.edara.domain.repo.UserRepo
import net.edara.domain.use_case.*

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideLogin(userRepo: UserRepo) = LoginUseCase(userRepo)

    @Provides
    fun provideServiceUseCase(servicesRepo: ServicesRepo) = GetAllServicesUseCase(servicesRepo)

    @Provides
    fun providePrintCase(servicesRepo: ServicesRepo) = PrintUseCase(servicesRepo)

    @Provides
    fun provideProfile(userRepo: UserRepo) = ProfileUseCase(userRepo)

    @Provides
    fun paymentUseCase(servicesRepo: ServicesRepo) = PaymentUseCase(servicesRepo)
}