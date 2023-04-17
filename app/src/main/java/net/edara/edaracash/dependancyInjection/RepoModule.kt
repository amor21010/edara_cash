package net.edara.edaracash.dependancyInjection

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.edara.data.remote.ApiService
import net.edara.data.repo.InsuranceServicesRepoImp
import net.edara.data.repo.PrivetServicesRepoImp
import net.edara.data.repo.UserRepoImp
import net.edara.domain.repo.InsuranceRepo
import net.edara.domain.repo.PrivetServicesRepo
import net.edara.domain.repo.UserRepo

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {
    @Provides
    fun provideLoginRepo(apiService: ApiService): UserRepo = UserRepoImp(apiService)

    @Provides
    fun provideServiceRepo(apiService: ApiService): PrivetServicesRepo =
        PrivetServicesRepoImp(apiService)

    @Provides
    fun provideInsuranceRepo(apiService: ApiService): InsuranceRepo =
        InsuranceServicesRepoImp(apiService)
}