package com.rkopylknu.minimaltodo.AppDefault

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rkopylknu.minimaltodo.util.SHARED_PREFS_NAME
import com.rkopylknu.minimaltodo.util.PREFS_THEME_KEY
import com.rkopylknu.minimaltodo.util.PREFS_THEME_LIGHT
import com.rkopylknu.minimaltodo.util.PREFS_TO_THEMES

open class AppDefaultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val themePrefs =
            getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            .getString(PREFS_THEME_KEY, PREFS_THEME_LIGHT)!!

        setTheme(PREFS_TO_THEMES[themePrefs]!!)
    }
}