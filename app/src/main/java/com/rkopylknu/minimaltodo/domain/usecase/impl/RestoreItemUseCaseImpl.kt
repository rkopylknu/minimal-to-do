package com.rkopylknu.minimaltodo.domain.usecase.impl

import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.data.repository.ToDoItemRepository
import com.rkopylknu.minimaltodo.domain.usecase.CreateAlarmUseCase
import com.rkopylknu.minimaltodo.domain.usecase.RestoreItemUseCase

class RestoreItemUseCaseImpl(
    private val toDoItemRepository: ToDoItemRepository,
    private val createAlarmUseCase: CreateAlarmUseCase
) : RestoreItemUseCase {

    override fun execute(item: ToDoItem, index: Int) {
        toDoItemRepository.mutate {
            add(index, item)
        }
        createAlarmUseCase.execute(item)
    }
}