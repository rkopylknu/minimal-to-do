package com.rkopylknu.minimaltodo.domain.usecase

import com.rkopylknu.minimaltodo.domain.model.ToDoItem

interface ValidateItemUseCase {

    fun execute(item: ToDoItem): Boolean
}