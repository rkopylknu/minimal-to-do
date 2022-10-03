package com.rkopylknu.minimaltodo.data.repository

import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import kotlinx.coroutines.flow.Flow

interface ToDoItemRepository {

    fun getAll(): Flow<List<ToDoItem>>

    fun getById(id: Long): Flow<ToDoItem?>

    fun getByPosition(position: Int): Flow<ToDoItem?>

    fun getMaxPosition(): Int?

    suspend fun insert(toDoItem: ToDoItem): Long

    suspend fun update(toDoItem: ToDoItem)

    suspend fun update(toDoItems: List<ToDoItem>)

    suspend fun deleteById(id: Long)
}