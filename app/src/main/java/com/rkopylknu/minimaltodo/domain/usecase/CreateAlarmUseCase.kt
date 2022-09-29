package com.rkopylknu.minimaltodo.domain.usecase

import com.rkopylknu.minimaltodo.domain.model.ToDoItem

interface CreateAlarmUseCase {

    fun execute(item: ToDoItem)
}