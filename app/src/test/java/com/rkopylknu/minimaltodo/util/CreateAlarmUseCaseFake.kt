package com.rkopylknu.minimaltodo.util

import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.domain.usecase.CreateAlarmUseCase

class CreateAlarmUseCaseFake : CreateAlarmUseCase {

    var wasExecuted: Boolean = false
        private set

    override fun execute(item: ToDoItem) {
        wasExecuted = true
    }
}