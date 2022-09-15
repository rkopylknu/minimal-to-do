package com.rkopylknu.minimaltodo.Settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceFragmentCompat
import com.rkopylknu.minimaltodo.Main.MainFragment
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.Utility.PreferenceKeys

class SettingsFragment :
    PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences_layout)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val preferenceKeys = PreferenceKeys(resources)
        if (key != preferenceKeys.night_mode_pref_key) return

        val themeEditor = requireActivity().getSharedPreferences(
            MainFragment.THEME_PREFERENCES,
            Context.MODE_PRIVATE
        ).edit()

        // We tell our MainLayout to recreate itself because mode has changed
        themeEditor.putBoolean(MainFragment.RECREATE_ACTIVITY, true)

        val checkBoxPreference =
            findPreference<CheckBoxPreference>(preferenceKeys.night_mode_pref_key)!!

        themeEditor.putString(
            MainFragment.THEME_SAVED,
            if (checkBoxPreference.isChecked) MainFragment.DARKTHEME
            else MainFragment.LIGHTTHEME
        )

        themeEditor.apply()
        requireActivity().recreate()
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences
            ?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences
            ?.unregisterOnSharedPreferenceChangeListener(this)
    }
}