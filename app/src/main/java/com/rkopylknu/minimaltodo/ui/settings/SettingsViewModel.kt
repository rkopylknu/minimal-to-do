package com.rkopylknu.minimaltodo.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.data.preferences.AppPreferences
import com.rkopylknu.minimaltodo.data.preferences.AppPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appPreferencesManager: AppPreferencesManager,
) : ViewModel() {

    val theme
        get() = appPreferencesManager.appPreferences.map { it.theme }

    val sortOrder
        get() = appPreferencesManager.appPreferences.map { it.sortOrder }

    fun onSwitchNightMode(block: () -> Unit = {}) {
        viewModelScope.launch {
            appPreferencesManager.setTheme(
                if (theme.first() == R.style.Theme_MinimalToDo_Light) {
                    R.style.Theme_MinimalToDo_Dark
                } else {
                    R.style.Theme_MinimalToDo_Light
                }
            )
            block()
        }
    }

    fun onChooseSortOrder(sortOrder: AppPreferences.SortOrder) {
        viewModelScope.launch {
            appPreferencesManager.setSortOrder(sortOrder)
        }
    }
}