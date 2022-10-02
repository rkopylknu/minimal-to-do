package com.rkopylknu.minimaltodo.data.repository

import com.rkopylknu.minimaltodo.data.storage.StoreRetrieveData
import com.rkopylknu.minimaltodo.data.storage.mutate
import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import javax.inject.Inject

class ToDoItemRepositoryImpl @Inject constructor(
    private val storeRetrieveData: StoreRetrieveData
) : ToDoItemRepository {

    override fun load(): List<ToDoItem> =
        storeRetrieveData.load()

    override fun save(toDoItems: List<ToDoItem>) =
        storeRetrieveData.save(toDoItems)

    override fun getValidId(): Long =
        storeRetrieveData.getValidId()

    override fun mutate(
        transformation: MutableList<ToDoItem>.() -> Unit
    ) {
        storeRetrieveData.mutate(transformation)
    }
}