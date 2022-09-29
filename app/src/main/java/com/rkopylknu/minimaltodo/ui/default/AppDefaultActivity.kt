package com.rkopylknu.minimaltodo.ui.default

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rkopylknu.minimaltodo.App

open class AppDefaultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appPreferencesManager =
            (application as App).appPreferencesManager

        setTheme(appPreferencesManager.get().theme)
    }
}