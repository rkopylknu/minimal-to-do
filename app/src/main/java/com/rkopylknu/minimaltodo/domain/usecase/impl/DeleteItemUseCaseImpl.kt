package com.rkopylknu.minimaltodo.domain.usecase.impl

import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.data.repository.ToDoItemRepository
import com.rkopylknu.minimaltodo.domain.usecase.DeleteAlarmUseCase
import com.rkopylknu.minimaltodo.domain.usecase.DeleteItemUseCase
import javax.inject.Inject

class DeleteItemUseCaseImpl @Inject constructor(
    private val toDoItemRepository: ToDoItemRepository,
    private val deleteAlarmUseCase: DeleteAlarmUseCase,
) : DeleteItemUseCase {

    override fun execute(item: ToDoItem) {
        val ensuredItem = toDoItemRepository.load()
            .find { it.id == item.id } ?: return

        toDoItemRepository.mutate {
            remove(ensuredItem)
        }
        deleteAlarmUseCase.execute(ensuredItem)
    }
}