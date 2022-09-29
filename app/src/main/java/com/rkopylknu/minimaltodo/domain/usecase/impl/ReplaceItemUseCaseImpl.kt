package com.rkopylknu.minimaltodo.domain.usecase.impl

import com.rkopylknu.minimaltodo.data.repository.ToDoItemRepository
import com.rkopylknu.minimaltodo.domain.usecase.ReplaceItemUseCase
import java.util.*

class ReplaceItemUseCaseImpl(
    private val toDoItemRepository: ToDoItemRepository
) : ReplaceItemUseCase {

    override fun execute(from: Int, to: Int) {
        val toDoItems = toDoItemRepository.load()
        if (from < to) {
            for (i in from until to) {
                Collections.swap(toDoItems, i, i + 1)
            }
        } else {
            for (i in from downTo to + 1) {
                Collections.swap(toDoItems, i, i - 1)
            }
        }
        toDoItemRepository.save(toDoItems)
    }
}