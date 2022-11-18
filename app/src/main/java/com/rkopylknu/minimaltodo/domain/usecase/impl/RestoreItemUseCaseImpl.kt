package com.rkopylknu.minimaltodo.domain.usecase.impl

import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.data.repository.ToDoItemRepository
import com.rkopylknu.minimaltodo.domain.usecase.CreateAlarmUseCase
import com.rkopylknu.minimaltodo.domain.usecase.RestoreItemUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RestoreItemUseCaseImpl @Inject constructor(
    private val toDoItemRepository: ToDoItemRepository,
    private val createAlarmUseCase: CreateAlarmUseCase
) : RestoreItemUseCase {

    override suspend fun execute(item: ToDoItem) {
        toDoItemRepository.insert(item)
        createAlarmUseCase.execute(item)
    }
}