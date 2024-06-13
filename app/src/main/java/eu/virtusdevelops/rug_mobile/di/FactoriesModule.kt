package eu.virtusdevelops.rug_mobile.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import eu.virtusdevelops.backendapi.ApiRequest
import eu.virtusdevelops.rug_mobile.repositories.AuthRepositoryImpl
import eu.virtusdevelops.rug_mobile.repositories.PackageHolderRepositoryImpl
import eu.virtusdevelops.rug_mobile.repositories.PackageRepositoryImpl
import eu.virtusdevelops.rug_mobile.repositories.SessionRepositoryImpl
import eu.virtusdevelops.rug_mobile.repositories.interfaces.AuthRepository
import eu.virtusdevelops.rug_mobile.repositories.interfaces.PackageHolderRepository
import eu.virtusdevelops.rug_mobile.repositories.interfaces.PackageRepository
import eu.virtusdevelops.rug_mobile.repositories.interfaces.SessionRepository

@Module
@InstallIn(SingletonComponent::class)
object FactoriesModule {

    @Provides
    fun authRepository(api: ApiRequest): AuthRepository {
        return AuthRepositoryImpl(api)
    }

    @Provides
    fun packageHolderRepository(api: ApiRequest): PackageHolderRepository{
        return PackageHolderRepositoryImpl(api)
    }

    @Provides
    fun packagesRepository(api: ApiRequest): PackageRepository{
        return PackageRepositoryImpl(api)
    }

    @Provides
    fun sessionsRepository(api: ApiRequest): SessionRepository{
        return SessionRepositoryImpl(api)
    }

}