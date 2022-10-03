package com.rkopylknu.minimaltodo.domain.usecase

import com.rkopylknu.minimaltodo.domain.model.ToDoItem

interface CreateItemUseCase {

    suspend fun execute(item: ToDoItem)
}