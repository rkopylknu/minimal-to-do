package com.rkopylknu.minimaltodo.di

import com.rkopylknu.minimaltodo.data.preferences.AppPreferencesManager
import com.rkopylknu.minimaltodo.data.preferences.AppPreferencesManagerImpl
import com.rkopylknu.minimaltodo.data.repository.ToDoItemRepository
import com.rkopylknu.minimaltodo.data.repository.ToDoItemRepositoryImpl
import com.rkopylknu.minimaltodo.domain.usecase.*
import com.rkopylknu.minimaltodo.domain.usecase.impl.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface BindingModule {

    @Binds
    fun bindAppPreferencesManager(impl: AppPreferencesManagerImpl): AppPreferencesManager

    @Binds
    @Singleton
    fun bindToDoItemRepository(impl: ToDoItemRepositoryImpl): ToDoItemRepository

    @Binds
    fun bindCreateAlarmUseCase(impl: CreateAlarmUseCaseImpl): CreateAlarmUseCase

    @Binds
    fun bindValidateItemUseCase(impl: ValidateItemUseCaseImpl): ValidateItemUseCase

    @Binds
    fun bindCreateItemUseCase(impl: CreateItemUseCaseImpl): CreateItemUseCase

    @Binds
    fun bindDeleteItemUseCase(impl: DeleteItemUseCaseImpl): DeleteItemUseCase

    @Binds
    fun bindUpdateItemUseCase(impl: UpdateItemUseCaseImpl): UpdateItemUseCase

    @Binds
    fun bindDisplayItemsUseCase(impl: DisplayItemsUseCaseImpl): DisplayItemsUseCase

    @Binds
    fun bindRestoreItemUseCase(impl: RestoreItemUseCaseImpl): RestoreItemUseCase
}