package com.williamfq.xhat.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    // Puedes añadir aquí otras vinculaciones específicas de este módulo si las necesitas.
}
