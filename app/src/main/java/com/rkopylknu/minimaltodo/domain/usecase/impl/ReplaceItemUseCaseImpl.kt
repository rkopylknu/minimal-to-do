package com.rkopylknu.minimaltodo.domain.usecase.impl

import com.rkopylknu.minimaltodo.data.repository.ToDoItemRepository
import com.rkopylknu.minimaltodo.domain.usecase.ReplaceItemUseCase
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject

class ReplaceItemUseCaseImpl @Inject constructor(
    private val toDoItemRepository: ToDoItemRepository,
) : ReplaceItemUseCase {

    override suspend fun execute(from: Int, to: Int) {
        val fromItem = toDoItemRepository.getByPosition(from).first()
        val toItem = toDoItemRepository.getByPosition(to).first()?.also {
            toDoItemRepository.update(
                it.copy(position = IMPOSSIBLE_POSITION)
            )
        }
        if (fromItem != null) {
            toDoItemRepository.update(fromItem.copy(position = to))
        }
        if (toItem != null) {
            toDoItemRepository.update(toItem.copy(position = from))
        }
    }

    companion object {

        private const val IMPOSSIBLE_POSITION = -1
    }
}