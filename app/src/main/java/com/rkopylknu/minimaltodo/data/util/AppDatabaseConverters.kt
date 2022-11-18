package com.rkopylknu.minimaltodo.data.util

import androidx.room.TypeConverter
import kotlinx.datetime.*

object AppDatabaseConverters {

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): Long? =
        dateTime?.toInstant(UtcOffset.ZERO)?.toEpochMilliseconds()

    @TypeConverter
    fun toLocalDateTime(milliseconds: Long?): LocalDateTime? =
        milliseconds?.let {
            Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC)
        }
}