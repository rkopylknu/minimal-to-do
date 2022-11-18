package com.rkopylknu.minimaltodo.util

import com.rkopylknu.minimaltodo.data.repository.ToDoItemRepository
import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ToDoItemRepositoryFake(
    initItems: List<ToDoItem> = emptyList()
) : ToDoItemRepository {

    private val items = initItems.toMutableList()

    override fun getAll(): Flow<List<ToDoItem>> =
        flowOf(items.toList())

    override fun getById(id: Long): Flow<ToDoItem?> =
        flowOf(items.firstOrNull { it.id == id })

    override suspend fun insert(toDoItem: ToDoItem): Long {
        items.add(toDoItem)
        return toDoItem.id
    }

    override suspend fun update(toDoItem: ToDoItem) {
        items.replaceAll { existing ->
            if (existing.id == toDoItem.id) {
                toDoItem
            } else {
                existing
            }
        }
    }

    override suspend fun update(toDoItems: List<ToDoItem>) {
        items.replaceAll { existing ->
            toDoItems
                .firstOrNull { it.id == existing.id }
                ?: existing
        }
    }

    override suspend fun deleteById(id: Long) {
        items.removeIf { it.id == id }
    }
}