package com.rkopylknu.minimaltodo.Reminder

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
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.Utility.ReminderService
import com.rkopylknu.minimaltodo.Utility.StoreRetrieveData
import com.rkopylknu.minimaltodo.Utility.ToDoItem
import com.rkopylknu.minimaltodo.Main.MainActivity
import com.rkopylknu.minimaltodo.Reminder.ReminderActivity.Companion.TO_DO_ITEM_KEY
import kotlinx.datetime.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ReminderFragment : Fragment(R.layout.fragment_reminder) {

    private lateinit var toDoItem: ToDoItem

    private lateinit var tvText: TextView
    private lateinit var spinnerSnoozeTime: AppCompatSpinner
    private lateinit var btnRemove: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        toDoItem = Json.decodeFromString(
            requireActivity().intent.getStringExtra(TO_DO_ITEM_KEY)!!
        )

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
            StoreRetrieveData.mutate(requireContext()) {
                remove(toDoItem)
            }
            cancelAlarm(toDoItem)
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

        StoreRetrieveData.mutate(requireContext()) {
            remove(toDoItem)
            add(newToDoItem)
        }

        cancelAlarm(toDoItem)
        createAlarm(newToDoItem)

        val navigateToMainActivityIntent =
            Intent(requireContext(), MainActivity::class.java)
        startActivity(navigateToMainActivityIntent)
    }

    private fun createAlarm(toDoItem: ToDoItem) {
        val createNotificationIntent =
            Intent(
                requireContext(),
                ReminderService::class.java
            ).apply {
                action = ReminderService.ACTION_CREATE
                putExtra(
                    ReminderService.TO_DO_ITEM_KEY,
                    Json.encodeToString(toDoItem)
                )
            }

        requireActivity().startService(createNotificationIntent)
    }

    private fun cancelAlarm(toDoItem: ToDoItem) {
        val cancelAlarmIntent = Intent(
            requireContext(),
            ReminderService::class.java
        ).apply {
            action = ReminderService.ACTION_CANCEL
            putExtra(
                ReminderService.TO_DO_ITEM_KEY,
                Json.encodeToString(toDoItem)
            )
        }
        requireActivity().startService(cancelAlarmIntent)
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