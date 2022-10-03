package com.rkopylknu.minimaltodo.domain.usecase

interface ReplaceItemUseCase {

    suspend fun execute(from: Int, to: Int)
}