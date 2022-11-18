package com.rkopylknu.minimaltodo.util

import com.rkopylknu.minimaltodo.data.preferences.AppPreferences
import com.rkopylknu.minimaltodo.data.preferences.AppPreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppPreferencesManagerFake : AppPreferencesManager {

    private val _appPreferences = MutableStateFlow(
        AppPreferences(
            theme = 0,
            sortOrder = AppPreferences.SortOrder.BY_TIME
        )
    )

    override val appPreferences: Flow<AppPreferences> =
        _appPreferences.asStateFlow()


    override suspend fun setTheme(theme: Int) {
        _appPreferences.value = _appPreferences.value.copy(
            theme = theme
        )
    }

    override suspend fun setSortOrder(sortOrder: AppPreferences.SortOrder) {
        _appPreferences.value = _appPreferences.value.copy(
            sortOrder = sortOrder
        )
    }
}