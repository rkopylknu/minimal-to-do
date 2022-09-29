package com.rkopylknu.minimaltodo.domain.usecase

import com.rkopylknu.minimaltodo.domain.model.ToDoItem

interface CreateItemUseCase {

    fun execute(item: ToDoItem)

    companion object {

        const val EMPTY_ID = 0L
        const val EMPTY_COLOR = 0
    }
}