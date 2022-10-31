package com.rkopylknu.minimaltodo.data.preferences

data class AppPreferences(
    val theme: Int,
    val sortOrder: SortOrder,
) {
    enum class SortOrder {
        BY_TIME,
        BY_NAME
    }
}