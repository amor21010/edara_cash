package net.edara.edaracash.dependancyInjection

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.edara.data.remote.ApiService
import net.edara.data.repo.LoginRepoImp
import net.edara.data.repo.ServicesRepoImp
import net.edara.domain.repo.LoginRepo
import net.edara.domain.repo.ServicesRepo

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {
    @Provides
    fun provideLoginRepo(apiService: ApiService): LoginRepo = LoginRepoImp(apiService)
    @Provides
    fun provideServiceRepo(apiService: ApiService): ServicesRepo = ServicesRepoImp(apiService)
}