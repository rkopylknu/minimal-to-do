package com.rkopylknu.minimaltodo.Utility

import com.rkopylknu.minimaltodo.Utility.LocalDateTimeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ToDoItem(
    val id: Long,
    val text: String,
    val description: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val reminder: LocalDateTime?,
    val color: Int
)