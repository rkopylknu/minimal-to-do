package com.rkopylknu.minimaltodo.domain.usecase.impl

import com.rkopylknu.minimaltodo.data.repository.ToDoItemRepository
import com.rkopylknu.minimaltodo.domain.usecase.DeleteItemUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DeleteItemUseCaseImpl @Inject constructor(
    private val toDoItemRepository: ToDoItemRepository,
) : DeleteItemUseCase {

    override suspend fun execute(id: Long) {
        val allItems = toDoItemRepository.getAll().first()
        val item = toDoItemRepository.getById(id).first()
        val repositionedItems = allItems
            .filter { it.position > (item?.position ?: 0) }
            .map { it.copy(position = it.position - 1) }

        toDoItemRepository.deleteById(id)
        toDoItemRepository.update(repositionedItems)
    }
}