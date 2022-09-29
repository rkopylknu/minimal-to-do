package com.rkopylknu.minimaltodo.ui.reminder

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.Fragment
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

    private lateinit var deleteItemUseCase: DeleteItemUseCase
    private lateinit var updateItemUseCase: UpdateItemUseCase

    private lateinit var toDoItem: ToDoItem

    private lateinit var tvText: TextView
    private lateinit var spinnerSnoozeTime: AppCompatSpinner
    private lateinit var btnRemove: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toDoItem = Json.decodeFromString(
            requireActivity().intent.getStringExtra(TO_DO_ITEM_KEY)!!
        )

        (requireActivity().application as App).run {
            deleteItemUseCase = DeleteItemUseCaseImpl(
                toDoItemRepository,
                DeleteAlarmUseCaseImpl(applicationContext)
            )
            updateItemUseCase = UpdateItemUseCaseImpl(
                toDoItemRepository,
                ValidateItemUseCaseImpl(),
                deleteItemUseCase,
                CreateAlarmUseCaseImpl(applicationContext)
            )
        }
    }

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

        tvText.text = toDoItem.text

        spinnerSnoozeTime.run {
            val snoozeOptionsStrings =
                resources.getStringArray(R.array.snooze_options)

            adapter = ArrayAdapter(
                requireContext(),
                R.layout.item_snooze_option,
                snoozeOptionsStrings
            )
        }

        btnRemove.setOnClickListener {
            deleteItemUseCase.execute(toDoItem)
            requireActivity().finish()
        }
    }

    private fun setDelayedReminder(minutes: Int) {
        val reminder = toDoItem.reminder ?: return

        val delayedReminder = TimeZone.currentSystemDefault().run {
            reminder.toInstant()
                .plus(DateTimePeriod(minutes = minutes), this)
                .toLocalDateTime()
        }
        val newToDoItem = toDoItem.copy(reminder = delayedReminder)

        updateItemUseCase.execute(toDoItem, newToDoItem)

        val navigateToMainActivityIntent =
            Intent(requireContext(), MainActivity::class.java)
        startActivity(navigateToMainActivityIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_reminder, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_done -> {
                val snoozeOption = SNOOZE_OPTIONS
                    .get(spinnerSnoozeTime.selectedItemPosition)
                setDelayedReminder(snoozeOption)
            }
            else -> return false
        }
        return true
    }

    companion object {

        private val SNOOZE_OPTIONS = listOf(10, 30, 60)
    }
}