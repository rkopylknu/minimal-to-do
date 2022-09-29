package com.rkopylknu.minimaltodo.domain.usecase

import com.rkopylknu.minimaltodo.domain.model.ToDoItem

interface DeleteAlarmUseCase {

    fun execute(item: ToDoItem)
}