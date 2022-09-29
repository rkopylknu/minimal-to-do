package com.rkopylknu.minimaltodo.domain.usecase

import com.rkopylknu.minimaltodo.domain.model.ToDoItem

interface UpdateItemUseCase {

    fun execute(oldItem: ToDoItem, newItem: ToDoItem)
}