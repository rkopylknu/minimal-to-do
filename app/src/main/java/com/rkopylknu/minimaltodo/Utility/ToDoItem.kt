package com.rkopylknu.minimaltodo.Utility

import org.json.JSONObject
import java.io.Serializable
import java.util.*


data class ToDoItem(
    val text: String,
    val description: String,
    val hasReminder: Boolean,
    val date: Date?,
    val color: Int = DEFAULT_COLOR,
    // IDs can be repeated due to random generation &
    // internal realization of seed generation
    val id: UUID = UUID.randomUUID(),
) : Serializable {

    constructor(jsonObject: JSONObject) : this(
        text = jsonObject.getString(TEXT_KEY),
        hasReminder = jsonObject.getBoolean(HAS_REMINDER_KEY),
        date = if (jsonObject.has(DATE_KEY)){
            Date(jsonObject.getLong(DATE_KEY))
        } else null,
        description = jsonObject.getString(DESCRIPTION_KEY),
        color = jsonObject.getInt(COLOR_KEY),
        id = UUID.fromString(jsonObject.getString(ID_KEY))
    )

    fun toJSON(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put(TEXT_KEY, text)
        jsonObject.put(DESCRIPTION_KEY, description)
        jsonObject.put(HAS_REMINDER_KEY, hasReminder)
        if (date != null) {
            jsonObject.put(DATE_KEY, date.time)
        }
        jsonObject.put(COLOR_KEY, color)
        jsonObject.put(ID_KEY, id.toString())
        return jsonObject
    }

    companion object {

        private const val DEFAULT_COLOR = 1677725

        // Serialization
        private const val TEXT_KEY = "text"
        private const val DESCRIPTION_KEY = "description"
        private const val HAS_REMINDER_KEY = "has_reminder"
        private const val COLOR_KEY = "color"
        private const val DATE_KEY = "date"
        private const val ID_KEY = "id"
    }
}