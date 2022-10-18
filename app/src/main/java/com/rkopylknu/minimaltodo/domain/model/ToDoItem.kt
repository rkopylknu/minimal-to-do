package com.rkopylknu.minimaltodo.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rkopylknu.minimaltodo.data.util.LocalDateTimeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "to_do_item"
)
data class ToDoItem(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "text")
    val text: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "is_prior")
    val isPrior: Boolean,
    @Serializable(with = LocalDateTimeSerializer::class)
    @ColumnInfo(name = "reminder")
    val reminder: LocalDateTime?,
    @ColumnInfo(name = "color")
    val color: Int = 0,
)