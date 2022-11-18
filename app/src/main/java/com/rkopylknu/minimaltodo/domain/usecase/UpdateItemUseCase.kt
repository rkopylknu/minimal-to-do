package com.rkopylknu.minimaltodo.domain.usecase

import com.rkopylknu.minimaltodo.domain.model.ToDoItem

interface UpdateItemUseCase {

    suspend fun execute(item: ToDoItem)
}