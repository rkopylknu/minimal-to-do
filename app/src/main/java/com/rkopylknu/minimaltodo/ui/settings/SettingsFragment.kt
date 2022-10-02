package com.rkopylknu.minimaltodo.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.rkopylknu.minimaltodo.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val viewModel: SettingsViewModel by viewModels()

    private lateinit var clNightMode: ConstraintLayout
    private lateinit var tvNightModeState: TextView
    private lateinit var cbNightMode: CheckBox

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        // Theme state is shown in onViewStateRestored
        // to override restored view state
        showThemeState(viewModel.theme)
    }

    private fun setupUI() {
        requireView().run {
            clNightMode = findViewById(R.id.cl_night_mode)
            tvNightModeState = findViewById(R.id.tv_night_mode_state)
            cbNightMode = findViewById(R.id.cb_night_mode)
        }

        clNightMode.setOnClickListener {
            viewModel.onSwitchNightMode()
            requireActivity().recreate()
        }
    }

    private fun showThemeState(theme: Int) {
        if (theme == R.style.Theme_MinimalToDo_Light) {
            tvNightModeState.text = getString(R.string.night_mode_off)
            cbNightMode.isChecked = false
        } else {
            tvNightModeState.text = getString(R.string.night_mode_on)
            cbNightMode.isChecked = true
        }
    }
}