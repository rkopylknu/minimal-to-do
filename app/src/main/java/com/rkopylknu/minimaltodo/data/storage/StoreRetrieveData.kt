package com.rkopylknu.minimaltodo.data.storage

import com.rkopylknu.minimaltodo.domain.model.ToDoItem

interface StoreRetrieveData {

    fun load(): List<ToDoItem>

    fun save(toDoItems: List<ToDoItem>)

    fun getValidId(): Long
}

fun StoreRetrieveData.mutate(
    transformation: MutableList<ToDoItem>.() -> Unit
) {
    val toDoItems = load().toMutableList()
    toDoItems.transformation()
    save(toDoItems)
}