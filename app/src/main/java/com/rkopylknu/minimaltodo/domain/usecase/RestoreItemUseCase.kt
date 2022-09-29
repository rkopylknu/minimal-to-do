package com.rkopylknu.minimaltodo.domain.usecase

import com.rkopylknu.minimaltodo.domain.model.ToDoItem

interface RestoreItemUseCase {

    fun execute(item: ToDoItem, index: Int)
}