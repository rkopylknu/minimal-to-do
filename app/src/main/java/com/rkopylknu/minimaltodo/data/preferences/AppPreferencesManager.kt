package com.rkopylknu.minimaltodo.data.preferences

import kotlinx.coroutines.flow.Flow

interface AppPreferencesManager {

    val appPreferences: Flow<AppPreferences>

    suspend fun setTheme(theme: Int)

    suspend fun setSortOrder(sortOrder: AppPreferences.SortOrder)
}