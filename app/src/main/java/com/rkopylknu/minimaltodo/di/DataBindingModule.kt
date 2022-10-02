package com.rkopylknu.minimaltodo.di

import com.rkopylknu.minimaltodo.data.preferences.AppPreferencesManager
import com.rkopylknu.minimaltodo.data.preferences.AppPreferencesManagerImpl
import com.rkopylknu.minimaltodo.data.repository.ToDoItemRepository
import com.rkopylknu.minimaltodo.data.repository.ToDoItemRepositoryImpl
import com.rkopylknu.minimaltodo.data.storage.StoreRetrieveData
import com.rkopylknu.minimaltodo.data.storage.StoreRetrieveDataImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataBindingModule {

    @Binds
    fun bindStoreRetrieveData(impl: StoreRetrieveDataImpl): StoreRetrieveData

    @Binds
    @Singleton
    fun bindToDoItemRepository(impl: ToDoItemRepositoryImpl): ToDoItemRepository

    @Binds
    fun bindAppPreferencesManager(impl: AppPreferencesManagerImpl): AppPreferencesManager
}