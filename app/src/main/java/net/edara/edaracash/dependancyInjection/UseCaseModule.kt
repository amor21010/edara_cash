package net.edara.edaracash.dependancyInjection

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.edara.domain.repo.LoginRepo
import net.edara.domain.repo.ServicesRepo
import net.edara.domain.use_case.GetAllServicesUseCase
import net.edara.domain.use_case.LoginUseCase
import net.edara.domain.use_case.PrintUseCase

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideLogin(loginRepo: LoginRepo) = LoginUseCase(loginRepo)

    @Provides
    fun provideServiceUseCase(servicesRepo: ServicesRepo) = GetAllServicesUseCase(servicesRepo)

    @Provides
    fun providePrintCase(servicesRepo: ServicesRepo) = PrintUseCase(servicesRepo)

   /*  @Provides
    fun providePayCase(servicesRepo: ServicesRepo) = PrintUseCase(servicesRepo)
*/
}