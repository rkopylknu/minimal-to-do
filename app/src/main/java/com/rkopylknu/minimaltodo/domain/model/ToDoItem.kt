package com.rkopylknu.minimaltodo.domain.model

import com.rkopylknu.minimaltodo.data.util.LocalDateTimeSerializer
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