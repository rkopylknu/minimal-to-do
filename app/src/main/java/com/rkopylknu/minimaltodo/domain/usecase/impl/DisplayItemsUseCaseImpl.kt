package com.rkopylknu.minimaltodo.domain.usecase.impl

import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.data.repository.ToDoItemRepository
import com.rkopylknu.minimaltodo.domain.usecase.DisplayItemsUseCase

class DisplayItemsUseCaseImpl(
    private val toDoItemRepository: ToDoItemRepository
) : DisplayItemsUseCase {

    override fun execute(): List<ToDoItem> =
        toDoItemRepository.load()
}