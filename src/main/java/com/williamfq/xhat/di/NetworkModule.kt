package com.williamfq.xhat.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    // Este módulo puede proporcionar otras dependencias específicas de la app.
}
