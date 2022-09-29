package com.rkopylknu.minimaltodo.domain.usecase

import com.rkopylknu.minimaltodo.domain.model.ToDoItem

interface DeleteItemUseCase {

    fun execute(item: ToDoItem)
}