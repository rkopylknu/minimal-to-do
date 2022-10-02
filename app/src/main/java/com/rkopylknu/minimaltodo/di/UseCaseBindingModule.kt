package com.rkopylknu.minimaltodo.di

import com.rkopylknu.minimaltodo.domain.usecase.*
import com.rkopylknu.minimaltodo.domain.usecase.impl.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface UseCaseBindingModule {

    @Binds
    fun bindCreateAlarmUseCase(impl: CreateAlarmUseCaseImpl): CreateAlarmUseCase

    @Binds
    fun bindValidateItemUseCase(impl: ValidateItemUseCaseImpl): ValidateItemUseCase

    @Binds
    fun bindCreateItemUseCase(impl: CreateItemUseCaseImpl): CreateItemUseCase

    @Binds
    fun bindDeleteAlarmUseCase(impl: DeleteAlarmUseCaseImpl): DeleteAlarmUseCase

    @Binds
    fun bindDeleteItemUseCase(impl: DeleteItemUseCaseImpl): DeleteItemUseCase

    @Binds
    fun bindUpdateItemUseCase(impl: UpdateItemUseCaseImpl): UpdateItemUseCase

    @Binds
    fun bindDisplayItemsUseCase(impl: DisplayItemsUseCaseImpl): DisplayItemsUseCase

    @Binds
    fun bindReplaceItemUseCase(impl: ReplaceItemUseCaseImpl): ReplaceItemUseCase

    @Binds
    fun bindRestoreItemUseCase(impl: RestoreItemUseCaseImpl): RestoreItemUseCase
}