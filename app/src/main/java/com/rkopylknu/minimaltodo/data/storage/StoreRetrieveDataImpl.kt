package com.rkopylknu.minimaltodo.data.storage

import android.content.Context
import android.os.Build
import android.util.Log
import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.util.STORAGE_FILE_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors
import javax.inject.Inject

@OptIn(ExperimentalSerializationApi::class)
class StoreRetrieveDataImpl @Inject constructor(
    @ApplicationContext private val appContext: Context,
) : StoreRetrieveData {

    private val serializer = ListSerializer(ToDoItem.serializer())

    override fun load(): List<ToDoItem> {
        val inputStream = appContext.openFileInput(STORAGE_FILE_NAME)

        val res = try {
            Json.decodeFromStream(serializer, inputStream)
        } catch (e: Exception) {
            save(emptyList())
            emptyList()
        }
        return res
    }

    override fun save(toDoItems: List<ToDoItem>) {
        val outputStream = appContext
            .openFileOutput(STORAGE_FILE_NAME, Context.MODE_PRIVATE)

        Json.encodeToStream(serializer, toDoItems, outputStream)
    }

    override fun getValidId(): Long =
        load().maxOfOrNull { it.id }
            ?.plus(1)
            ?: FIRST_ID

    companion object {

        private const val FIRST_ID = 0L
    }
}