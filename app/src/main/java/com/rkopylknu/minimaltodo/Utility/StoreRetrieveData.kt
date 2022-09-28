package com.rkopylknu.minimaltodo.Utility

import android.content.Context
import com.rkopylknu.minimaltodo.util.STORAGE_FILE_NAME
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream

object StoreRetrieveData {

    private val serializer = ListSerializer(ToDoItem.serializer())

    fun save(context: Context, toDoItems: List<ToDoItem>) {
        val outputStream = context
            .openFileOutput(STORAGE_FILE_NAME, Context.MODE_PRIVATE)
        Json.encodeToStream(serializer, toDoItems, outputStream)
    }

    fun mutate(
        context: Context,
        transformation: MutableList<ToDoItem>.() -> Unit
    ) {
        val toDoItems = load(context).toMutableList()
        toDoItems.transformation()
        save(context, toDoItems)
    }

    fun load(context: Context): List<ToDoItem> {
        val inputStream = context.openFileInput(STORAGE_FILE_NAME)

        val res = try {
            Json.decodeFromStream(serializer, inputStream)
        } catch (e: Exception) {
            save(context, emptyList())
            emptyList()
        }
        return res
    }

    fun getLastToDoItemId(context: Context): Long? =
        load(context).lastOrNull()?.id
}