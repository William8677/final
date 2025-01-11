package com.williamfq.xhat.ui

import com.williamfq.data.remote.XhatApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideXhatApiService(retrofit: Retrofit): XhatApiService =
        retrofit.create(XhatApiService::class.java)

    // Otros métodos permanecen iguales.
}
