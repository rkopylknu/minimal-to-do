package com.rkopylknu.minimaltodo.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.util.APP_PREFERENCES_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppPreferencesManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AppPreferencesManager {

    override fun get(): AppPreferences {
        val prefs = context.getSharedPreferences(
            APP_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
        return mapAppPreferences(prefs)
    }

    override fun setTheme(theme: Int) {
        val prefs = context.getSharedPreferences(
            APP_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
        prefs.edit(commit = true) {
            putInt(Keys.THEME, theme)
        }
    }

    private fun mapAppPreferences(prefs: SharedPreferences) =
        AppPreferences(
            theme = prefs.getInt(Keys.THEME, R.style.Theme_MinimalToDo_Light)
        )

    private object Keys {

        const val THEME = "theme"
    }
}