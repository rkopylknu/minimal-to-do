package com.rkopylknu.minimaltodo.AddToDo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.Utility.ReminderService
import com.rkopylknu.minimaltodo.Utility.StoreRetrieveData
import com.rkopylknu.minimaltodo.Utility.ToDoItem
import com.rkopylknu.minimaltodo.AddToDo.AddToDoActivity.Companion.TO_DO_ITEM_KEY
import com.rkopylknu.minimaltodo.util.TO_DO_ITEM_COLORS
import com.rkopylknu.minimaltodo.util.getColorCompat
import kotlinx.datetime.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AddToDoFragment : Fragment(R.layout.fragment_add_to_do) {

    private var toDoItem: ToDoItem? = null

    private var reminder: LocalDateTime? = null

    private lateinit var etText: EditText
    private lateinit var etDescription: EditText
    private lateinit var switchAddReminder: SwitchCompat
    private lateinit var etReminderDate: EditText
    private lateinit var etReminderTime: EditText
    private lateinit var fabSaveToDoItem: FloatingActionButton
    private lateinit var btnCopy: Button
    private lateinit var clReminder: ConstraintLayout
    private lateinit var tvReminderSet: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toDoItemJson = requireActivity().intent?.getStringExtra(TO_DO_ITEM_KEY)
        toDoItem = toDoItemJson?.let { Json.decodeFromString(it) }

        setupUI()
    }

    private fun setupUI() {
        requireView().run {
            etText = findViewById(R.id.et_text)
            etDescription = findViewById(R.id.et_description)
            switchAddReminder = findViewById(R.id.switch_add_reminder)
            etReminderDate = findViewById(R.id.et_reminder_date)
            etReminderTime = findViewById(R.id.et_reminder_time)
            fabSaveToDoItem = findViewById(R.id.fab_save_to_do_item)
            btnCopy = findViewById(R.id.btn_copy)
            clReminder = findViewById(R.id.cl_reminder)
            tvReminderSet = findViewById(R.id.tv_reminder_set)
        }

        toDoItem?.run {
            etText.setText(text)
            etDescription.setText(description)
            this@AddToDoFragment.reminder = reminder
            displayReminder()
        }

        switchAddReminder.run {
            isChecked = reminder != null
            setOnClickListener {
                if (switchAddReminder.isChecked) {
                    if (reminder == null) {
                        reminder = createDefaultReminder()
                    }
                } else {
                    reminder = null
                }

                displayReminder()
            }
        }

        fabSaveToDoItem.setOnClickListener {
            trySaveToDoItem()
            requireActivity().finish()
        }

        btnCopy.setOnClickListener {
            copyToDoItemToClipboard()
        }

        etReminderDate.setOnClickListener {
            val now = Clock.System.todayIn(TimeZone.currentSystemDefault())
            DatePickerDialog(
                requireContext(), dateTimePickerListener,
                now.year, now.monthNumber, now.dayOfMonth
            ).show()
        }

        etReminderTime.setOnClickListener {
            val now = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault()).time

            TimePickerDialog(
                requireContext(), dateTimePickerListener,
                now.hour, now.minute,
                DateFormat.is24HourFormat(requireContext())
            ).show()
        }
    }

    private fun createDefaultReminder(): LocalDateTime {
        val atNextHour = Clock.System.now()
            .plus(DateTimePeriod(hours = 1), TimeZone.UTC)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        return LocalDateTime(
            atNextHour.date,
            LocalTime(atNextHour.hour + 1, minute = 0)
        )
    }

    private fun displayReminder() {
        etReminderDate.setText(reminder?.date?.toString() ?: "")
        etReminderTime.setText(reminder?.time?.toString() ?: "")
        tvReminderSet.text = reminder?.let { reminder ->
            val now = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())

            if (reminder >= now) {
                tvReminderSet.setTextColor(
                    getColorCompat(R.color.secondary_text)
                )
                getString(R.string.reminder_set_for, reminder.toString())
            } else {
                tvReminderSet.setTextColor(Color.RED)
                getString(R.string.entered_date_in_past)
            }
        } ?: ""

        // show / hide reminder layout
        clReminder.visibility =
            if (reminder != null) View.VISIBLE else View.INVISIBLE

        clReminder.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                if (reminder != null) R.anim.fade_in_500
                else R.anim.fade_out_500
            )
        )
    }

    private val dateTimePickerListener = object :
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

        override fun onDateSet(picker: DatePicker?, year: Int, month: Int, day: Int) {
            reminder = reminder?.run {
                LocalDateTime(
                    date = LocalDate(year, month, day),
                    time = time
                )
            }
            displayReminder()
        }

        override fun onTimeSet(picker: TimePicker?, hour: Int, minute: Int) {
            reminder = reminder?.run {
                LocalDateTime(
                    date = date,
                    time = LocalTime(hour, minute)
                )
            }
            displayReminder()
        }
    }

    private fun isValidInput(): Boolean {
        if (etText.text.isNullOrEmpty()) return false

        reminder?.let { reminder ->
            val now = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())

            if (reminder < now) return false
        }

        return true
    }

    private fun trySaveToDoItem() {
        if (!isValidInput()) return

        val lastStoredId = StoreRetrieveData.getLastToDoItemId(requireContext())

        val newToDoItem = ToDoItem(
            id = toDoItem?.id
                ?: (lastStoredId?.plus(1))
                ?: TO_DO_ITEM_DEFAULT_ID,
            text = etText.text.toString(),
            description = etDescription.text.toString(),
            reminder = reminder,
            color = toDoItem?.color
                ?: TO_DO_ITEM_COLORS.random()
        )

        StoreRetrieveData.mutate(requireContext()) {
            remove(toDoItem)
            add(newToDoItem)
        }
        toDoItem?.let { tryCancelAlarm(it) }
        tryCreateAlarm(newToDoItem)
    }

    private fun tryCreateAlarm(toDoItem: ToDoItem) {
        if (toDoItem.reminder == null) return

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

    private fun tryCancelAlarm(toDoItem: ToDoItem) {
        if (toDoItem.reminder == null) return

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

    private fun copyToDoItemToClipboard() {
        val clipboard = requireActivity()
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val toDoItemString = "Title : ${etText.text}\n" +
                "Description : ${etDescription.text}\n" +
                " -Copied From MinimalToDo"

        clipboard.setPrimaryClip(
            ClipData.newPlainText("text", toDoItemString)
        )

        Toast.makeText(
            context,
            "Copied To Clipboard!",
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {

        const val TO_DO_ITEM_DEFAULT_ID = 0L
    }
}