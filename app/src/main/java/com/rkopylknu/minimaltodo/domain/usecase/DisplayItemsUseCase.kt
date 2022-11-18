package com.rkopylknu.minimaltodo.domain.usecase

import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import kotlinx.coroutines.flow.Flow

interface DisplayItemsUseCase {

    fun execute(): Flow<List<ToDoItem>>
}