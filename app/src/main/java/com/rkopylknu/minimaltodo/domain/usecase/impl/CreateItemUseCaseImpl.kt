package com.rkopylknu.minimaltodo.domain.usecase.impl

import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.data.repository.ToDoItemRepository
import com.rkopylknu.minimaltodo.domain.usecase.CreateAlarmUseCase
import com.rkopylknu.minimaltodo.domain.usecase.CreateItemUseCase
import com.rkopylknu.minimaltodo.domain.usecase.ValidateItemUseCase
import com.rkopylknu.minimaltodo.util.TO_DO_ITEM_COLORS
import javax.inject.Inject

class CreateItemUseCaseImpl @Inject constructor(
    private val toDoItemRepository: ToDoItemRepository,
    private val createAlarmUseCase: CreateAlarmUseCase,
    private val validateItemUseCase: ValidateItemUseCase
) : CreateItemUseCase {

    override fun execute(item: ToDoItem) {
        if (!validateItemUseCase.execute(item)) return

        val newItem = item.copy(
            id = toDoItemRepository.getValidId(),
            color = TO_DO_ITEM_COLORS.random()
        )

        toDoItemRepository.mutate {
            add(newItem)
        }
        createAlarmUseCase.execute(newItem)
    }
}