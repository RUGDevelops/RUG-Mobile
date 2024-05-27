package eu.virtusdevelops.rug_mobile.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import eu.virtusdevelops.backendapi.Api
import eu.virtusdevelops.backendapi.ApiRequest
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {




    @Provides
    @Singleton
    fun ApiProvider(): Api{
        return Api
    }


    @Provides
    @Singleton
    fun provideAPI(api: Api): ApiRequest{
        return api.api
    }





}