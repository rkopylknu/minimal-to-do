package com.rkopylknu.minimaltodo.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.databinding.FragmentMainBinding
import com.rkopylknu.minimaltodo.databinding.FragmentReminderBinding
import com.rkopylknu.minimaltodo.databinding.FragmentSettingsBinding
import com.rkopylknu.minimaltodo.util.collectOnLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val viewModel: SettingsViewModel by viewModels()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = checkNotNull(_binding)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        setupUI()
        setupObservers()
    }

    private fun setupUI() = binding.run {
        clNightMode.setOnClickListener {
            viewModel.onSwitchNightMode {
                requireActivity().recreate()
            }
        }
    }

    private fun setupObservers() {
        viewModel.theme.collectOnLifecycle(
            viewLifecycleOwner,
            collector = ::showThemeState
        )
    }

    private fun showThemeState(theme: Int) = binding.run {
        if (theme == R.style.Theme_MinimalToDo_Light) {
            tvNightModeState.text = getString(R.string.night_mode_off)
            cbNightMode.isChecked = false
        } else {
            tvNightModeState.text = getString(R.string.night_mode_on)
            cbNightMode.isChecked = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}