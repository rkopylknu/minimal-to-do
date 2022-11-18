package com.rkopylknu.minimaltodo.domain.usecase.impl

import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.data.repository.ToDoItemRepository
import com.rkopylknu.minimaltodo.domain.usecase.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UpdateItemUseCaseImpl @Inject constructor(
    private val toDoItemRepository: ToDoItemRepository,
    private val validateItemUseCase: ValidateItemUseCase,
    private val createAlarmUseCase: CreateAlarmUseCase,
) : UpdateItemUseCase {

    override suspend fun execute(item: ToDoItem) {
        if (!validateItemUseCase.execute(item)) return

        toDoItemRepository.update(item)
        createAlarmUseCase.execute(item)
    }
}