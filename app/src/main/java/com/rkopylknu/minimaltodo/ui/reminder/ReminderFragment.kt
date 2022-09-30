package com.rkopylknu.minimaltodo.ui.reminder

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.rkopylknu.minimaltodo.App
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.domain.usecase.impl.*

class ReminderFragment(
    private val toDoItem: ToDoItem,
) : Fragment(R.layout.fragment_reminder), MenuProvider {

    private val viewModel: ReminderViewModel by viewModels {
        (requireActivity().application as App).run {
            val deleteItemUseCase = DeleteItemUseCaseImpl(
                toDoItemRepository,
                DeleteAlarmUseCaseImpl(applicationContext)
            )
            ReminderViewModel.Factory(
                deleteItemUseCase,
                UpdateItemUseCaseImpl(
                    toDoItemRepository,
                    ValidateItemUseCaseImpl(),
                    deleteItemUseCase,
                    CreateAlarmUseCaseImpl(applicationContext)
                ),
                toDoItem
            )
        }
    }

    private lateinit var tvText: TextView
    private lateinit var spinnerSnoozeTime: AppCompatSpinner
    private lateinit var btnRemove: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().addMenuProvider(this, viewLifecycleOwner)

        setupUI()
    }

    private fun setupUI() {
        requireView().run {
            tvText = findViewById(R.id.tv_text)
            spinnerSnoozeTime = findViewById(R.id.spinner_snooze_time)
            btnRemove = findViewById(R.id.btn_remove)
        }

        tvText.text = viewModel.toDoItem.text

        spinnerSnoozeTime.run {
            val snoozeOptionsStrings =
                resources.getStringArray(R.array.snooze_options)

            adapter = ArrayAdapter(
                requireContext(),
                R.layout.item_snooze_option,
                snoozeOptionsStrings
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
            parentFragmentManager.popBackStack()
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_reminder, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.item_done -> {
                viewModel.onDelayReminder()
                parentFragmentManager.popBackStack()
            }
            else -> return false
        }
        return true
    }
}