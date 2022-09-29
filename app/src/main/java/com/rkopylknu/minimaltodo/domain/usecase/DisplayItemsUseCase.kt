package com.rkopylknu.minimaltodo.domain.usecase

import com.rkopylknu.minimaltodo.domain.model.ToDoItem

interface DisplayItemsUseCase {

    fun execute(): List<ToDoItem>
}