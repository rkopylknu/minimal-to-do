package com.rkopylknu.minimaltodo.ui.reminder

import android.content.Intent
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.rkopylknu.minimaltodo.App
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.domain.usecase.DeleteItemUseCase
import com.rkopylknu.minimaltodo.domain.usecase.UpdateItemUseCase
import com.rkopylknu.minimaltodo.domain.usecase.impl.*
import com.rkopylknu.minimaltodo.ui.main.MainActivity
import com.rkopylknu.minimaltodo.ui.reminder.ReminderActivity.Companion.TO_DO_ITEM_KEY
import kotlinx.datetime.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ReminderFragment : Fragment(R.layout.fragment_reminder) {

    private val viewModel: ReminderViewModel by viewModels {
        val toDoItem = Json.decodeFromString<ToDoItem>(
            requireActivity().intent.getStringExtra(TO_DO_ITEM_KEY)!!
        )

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

        setHasOptionsMenu(true)

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
                    index: Int, p3: Long
                ) {
                    viewModel.onSetSnoozeOption(index)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }

        btnRemove.setOnClickListener {
            viewModel.onDeleteItem()
            requireActivity().finish()
        }
    }

    private fun setDelayedReminder() {
        viewModel.onDelayReminder()

        val navigateToMainActivityIntent =
            Intent(requireContext(), MainActivity::class.java)
        startActivity(navigateToMainActivityIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_reminder, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_done -> setDelayedReminder()
            else -> return false
        }
        return true
    }
}