package com.rkopylknu.minimaltodo.domain.usecase

import com.rkopylknu.minimaltodo.domain.model.ToDoItem

interface RestoreItemUseCase {

    suspend fun execute(item: ToDoItem)
}