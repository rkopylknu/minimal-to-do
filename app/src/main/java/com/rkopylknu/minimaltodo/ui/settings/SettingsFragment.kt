package com.rkopylknu.minimaltodo.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.data.preferences.AppPreferences
import com.rkopylknu.minimaltodo.databinding.FragmentSettingsBinding
import com.rkopylknu.minimaltodo.util.collectOnLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val viewModel: SettingsViewModel by viewModels()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val sortOrderToText by lazy {
        mapOf<AppPreferences.SortOrder, String>(
            AppPreferences.SortOrder.BY_TIME to getString(R.string.by_time),
            AppPreferences.SortOrder.BY_NAME to getString(R.string.by_name)
        )
    }

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
        llSortOrder.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.sort_order)
                .setItems(
                    sortOrderToText.values.toTypedArray()
                ) { _, index ->
                    val clickedText = sortOrderToText.values.elementAt(index)
                    val sortOrder = sortOrderToText.firstNotNullOf {
                        if (it.value == clickedText) {
                            it.key
                        } else {
                            null
                        }
                    }
                    viewModel.onChooseSortOrder(sortOrder)
                }
                .show()
        }
    }

    private fun setupObservers() {
        viewModel.theme.collectOnLifecycle(
            viewLifecycleOwner,
            collector = ::showThemeState
        )
        viewModel.sortOrder.collectOnLifecycle(
            viewLifecycleOwner,
            collector = ::showSortOrder
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

    private fun showSortOrder(
        sortOrder: AppPreferences.SortOrder,
    ) = binding.run {
        tvSortOrderValue.text = sortOrderToText[sortOrder]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}