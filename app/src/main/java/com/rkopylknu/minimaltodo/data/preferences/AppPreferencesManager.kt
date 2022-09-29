package com.rkopylknu.minimaltodo.data.preferences

interface AppPreferencesManager {

    fun get(): AppPreferences

    fun setTheme(theme: Int)
}