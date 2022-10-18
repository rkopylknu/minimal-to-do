package com.rkopylknu.minimaltodo.data.repository

import com.rkopylknu.minimaltodo.data.room.ToDoItemDao
import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import javax.inject.Inject

class ToDoItemRepositoryImpl @Inject constructor(
    private val toDoItemDao: ToDoItemDao
) : ToDoItemRepository {

    override fun getAll() = toDoItemDao.getAll()

    override fun getById(id: Long) =
        toDoItemDao.getById(id)

    override suspend fun insert(toDoItem: ToDoItem) =
        toDoItemDao.insert(toDoItem)

    override suspend fun update(toDoItem: ToDoItem) =
        toDoItemDao.update(toDoItem)

    override suspend fun update(toDoItems: List<ToDoItem>) =
        toDoItemDao.update(toDoItems)

    override suspend fun deleteById(id: Long) =
        toDoItemDao.deleteById(id)
}