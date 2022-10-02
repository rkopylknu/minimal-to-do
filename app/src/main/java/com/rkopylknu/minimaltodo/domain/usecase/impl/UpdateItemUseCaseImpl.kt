package com.rkopylknu.minimaltodo.domain.usecase.impl

import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.data.repository.ToDoItemRepository
import com.rkopylknu.minimaltodo.domain.usecase.*
import javax.inject.Inject

class UpdateItemUseCaseImpl @Inject constructor(
    private val toDoItemRepository: ToDoItemRepository,
    private val validateItemUseCase: ValidateItemUseCase,
    private val deleteItemUseCase: DeleteItemUseCase,
    private val createAlarmUseCase: CreateAlarmUseCase
) : UpdateItemUseCase {

    override fun execute(oldItem: ToDoItem, item: ToDoItem) {
        if(!validateItemUseCase.execute(item)) return

        deleteItemUseCase.execute(oldItem)

        val newItem = item.copy(
            id = oldItem.id,
            color = oldItem.color
        )
        toDoItemRepository.mutate {
            add(newItem)
        }
        createAlarmUseCase.execute(newItem)
    }
}