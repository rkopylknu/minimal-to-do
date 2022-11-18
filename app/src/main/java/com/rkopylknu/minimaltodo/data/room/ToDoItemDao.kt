package com.rkopylknu.minimaltodo.data.room

import androidx.room.*
import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoItemDao {

    @Query("SELECT * FROM to_do_item ORDER BY is_prior DESC")
    fun getAll(): Flow<List<ToDoItem>>

    @Query("SELECT * FROM to_do_item WHERE id = :id")
    fun getById(id: Long): Flow<ToDoItem>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(toDoItem: ToDoItem): Long

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun update(toDoItem: ToDoItem)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun update(toDoItems: List<ToDoItem>)

    @Query("DELETE FROM to_do_item WHERE id = :id")
    suspend fun deleteById(id: Long)
}