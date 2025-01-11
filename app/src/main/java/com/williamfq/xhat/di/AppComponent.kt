package com.williamfq.xhat.di

import android.app.Application
import com.williamfq.data.di.DatabaseModule
import com.williamfq.data.di.RepositoryModule
import com.williamfq.xhat.XhatApplication
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class, RepositoryModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }

    fun inject(application: XhatApplication)
}