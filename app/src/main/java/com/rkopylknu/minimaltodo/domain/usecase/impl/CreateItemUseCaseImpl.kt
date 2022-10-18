package com.rkopylknu.minimaltodo.domain.usecase.impl

import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.data.repository.ToDoItemRepository
import com.rkopylknu.minimaltodo.domain.usecase.CreateAlarmUseCase
import com.rkopylknu.minimaltodo.domain.usecase.CreateItemUseCase
import com.rkopylknu.minimaltodo.domain.usecase.ValidateItemUseCase
import com.rkopylknu.minimaltodo.util.TO_DO_ITEM_COLORS
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CreateItemUseCaseImpl @Inject constructor(
    private val toDoItemRepository: ToDoItemRepository,
    private val createAlarmUseCase: CreateAlarmUseCase,
    private val validateItemUseCase: ValidateItemUseCase
) : CreateItemUseCase {

    override suspend fun execute(item: ToDoItem) {
        if (!validateItemUseCase.execute(item)) return

        val newItem = item.copy(
            color = TO_DO_ITEM_COLORS.random(),
        )
        val id = toDoItemRepository.insert(newItem)
        val insertedItem = toDoItemRepository.getById(id).first()
        insertedItem?.let { createAlarmUseCase.execute(it) }
    }
}