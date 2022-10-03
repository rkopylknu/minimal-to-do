package com.rkopylknu.minimaltodo.domain.usecase

interface DeleteItemUseCase {

    suspend fun execute(id: Long)
}