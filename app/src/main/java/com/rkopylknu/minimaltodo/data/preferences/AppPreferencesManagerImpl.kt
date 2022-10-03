package com.rkopylknu.minimaltodo.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.rkopylknu.minimaltodo.R
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppPreferencesManagerImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AppPreferencesManager {

    override val appPreferences =
        dataStore.data.map { mapAppPreferences(it) }

    override suspend fun setTheme(theme: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.THEME] = theme
        }
    }

    private fun mapAppPreferences(prefs: Preferences) =
        AppPreferences(
            theme = prefs[Keys.THEME] ?: R.style.Theme_MinimalToDo_Light
        )

    private object Keys {

        val THEME = intPreferencesKey("theme")
    }
}