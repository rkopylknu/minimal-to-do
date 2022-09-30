package com.rkopylknu.minimaltodo.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.data.preferences.AppPreferencesManager

class SettingsViewModel(
    private val appPreferencesManager: AppPreferencesManager
) : ViewModel() {

    val theme get() = appPreferencesManager.get().theme

    fun onSwitchNightMode() {
        appPreferencesManager.setTheme(
            if (theme == R.style.Theme_MinimalToDo_Light) {
                R.style.Theme_MinimalToDo_Dark
            } else {
                R.style.Theme_MinimalToDo_Light
            }
        )
    }

    class Factory(
        private val appPreferencesManager: AppPreferencesManager
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                return SettingsViewModel(appPreferencesManager) as T
            } else {
                throw IllegalArgumentException("Unexpected ViewModel class")
            }
        }
    }
}