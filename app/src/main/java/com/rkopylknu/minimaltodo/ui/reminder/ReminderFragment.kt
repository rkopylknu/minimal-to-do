package com.rkopylknu.minimaltodo.ui.reminder

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import com.rkopylknu.minimaltodo.databinding.FragmentReminderBinding
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.domain.usecase.impl.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@AndroidEntryPoint
class ReminderFragment : Fragment(R.layout.fragment_reminder), MenuProvider {

    private val args: ReminderFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelDaggerFactory: ReminderViewModel.DaggerFactory

    private val viewModel: ReminderViewModel by viewModels {
        ReminderViewModel.getFactory(
            viewModelDaggerFactory,
            Json.decodeFromString(args.toDoItemJson)
        )
    }

    private var _binding: FragmentReminderBinding? = null
    private val binding get() = checkNotNull(_binding)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentReminderBinding.bind(view)

        requireActivity().addMenuProvider(this, viewLifecycleOwner)

        setupUI()
    }

    private fun setupUI() = binding.run {
        tvText.text = viewModel.toDoItem.text

        spinnerSnoozeTime.run {
            val snoozeOptions =
                resources.getStringArray(R.array.snooze_options)

            adapter = ArrayAdapter(
                requireContext(),
                R.layout.item_snooze_option,
                snoozeOptions
            )

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    p0: AdapterView<*>?, p1: View?,
                    index: Int, p3: Long,
                ) {
                    viewModel.onSetSnoozeOption(index)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }

        btnRemove.setOnClickListener {
            viewModel.onDeleteItem()
            findNavController().navigate(
                ReminderFragmentDirections
                    .actionReminderFragmentToMainFragment()
            )
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_reminder, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.item_done -> {
                viewModel.onDelayReminder()
                findNavController().navigate(
                    ReminderFragmentDirections
                        .actionReminderFragmentToMainFragment()
                )
            }
            else -> return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}