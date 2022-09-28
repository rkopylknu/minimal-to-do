package com.rkopylknu.minimaltodo.Settings

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.util.*

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var clNightMode: ConstraintLayout
    private lateinit var tvNightModeState: TextView
    private lateinit var cbNightMode: CheckBox

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        val theme = requireActivity()
            .getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            .getString(PREFS_THEME_KEY, PREFS_THEME_LIGHT)!!

        // Theme state is shown in onViewStateRestored
        // to override restored view state
        showThemeState(theme)
    }

    private fun setupUI() {
        requireView().run {
            clNightMode = findViewById(R.id.cl_night_mode)
            tvNightModeState = findViewById(R.id.tv_night_mode_state)
            cbNightMode = findViewById(R.id.cb_night_mode)
        }

        clNightMode.setOnClickListener {
            switchTheme()
            requireActivity().recreate()
        }
    }

    private fun switchTheme() {
        val preferences = requireActivity()
            .getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val theme = preferences.getString(PREFS_THEME_KEY, PREFS_THEME_LIGHT)!!

        val oppositeTheme =
            if (theme == PREFS_THEME_LIGHT) PREFS_THEME_DARK else PREFS_THEME_LIGHT

        preferences.edit(commit = true) {
            putString(PREFS_THEME_KEY, oppositeTheme)
        }
    }

    private fun showThemeState(theme: String) {
        if (theme == PREFS_THEME_LIGHT) {
            tvNightModeState.text = getString(R.string.night_mode_off)
            cbNightMode.isChecked = false
        } else {
            tvNightModeState.text = getString(R.string.night_mode_on)
            cbNightMode.isChecked = true
        }
    }
}