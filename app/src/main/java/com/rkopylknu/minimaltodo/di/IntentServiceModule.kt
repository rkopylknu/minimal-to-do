package com.rkopylknu.minimaltodo.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object IntentServiceModule {

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope = CoroutineScope(Job())
}