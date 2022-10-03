package com.rkopylknu.minimaltodo.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rkopylknu.minimaltodo.data.util.AppDatabaseConverters
import com.rkopylknu.minimaltodo.domain.model.ToDoItem

@Database(
    entities = [ToDoItem::class],
    version = 1
)
@TypeConverters(AppDatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract val toDoItemDao: ToDoItemDao
}