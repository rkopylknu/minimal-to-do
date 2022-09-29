package com.rkopylknu.minimaltodo.domain.usecase.impl

import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.domain.usecase.ValidateItemUseCase
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ValidateItemUseCaseImpl : ValidateItemUseCase {

    override fun execute(item: ToDoItem): Boolean = item.run {
        if (text.isEmpty()) return false

        if (reminder != null) {
            val now = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())

            if (reminder < now) return false
        }

        return true
    }
}