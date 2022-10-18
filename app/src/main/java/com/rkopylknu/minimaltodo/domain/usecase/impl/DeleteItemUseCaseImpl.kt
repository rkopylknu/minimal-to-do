package com.rkopylknu.minimaltodo.domain.usecase.impl

import com.rkopylknu.minimaltodo.data.repository.ToDoItemRepository
import com.rkopylknu.minimaltodo.domain.usecase.DeleteItemUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DeleteItemUseCaseImpl @Inject constructor(
    private val toDoItemRepository: ToDoItemRepository,
) : DeleteItemUseCase {

    override suspend fun execute(id: Long) {
        toDoItemRepository.deleteById(id)
    }
}