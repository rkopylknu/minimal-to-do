package com.rkopylknu.minimaltodo.ui.add

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import com.rkopylknu.minimaltodo.App
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.domain.usecase.CreateItemUseCase
import com.rkopylknu.minimaltodo.domain.usecase.UpdateItemUseCase
import com.rkopylknu.minimaltodo.domain.usecase.impl.*
import com.rkopylknu.minimaltodo.ui.add.AddToDoActivity.Companion.TO_DO_ITEM_KEY
import com.rkopylknu.minimaltodo.util.getColorCompat
import kotlinx.datetime.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class AddToDoFragment : Fragment(R.layout.fragment_add_to_do) {

    private lateinit var createItemUseCase: CreateItemUseCase
    private lateinit var updateItemUseCase: UpdateItemUseCase

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (requireActivity().application as App).run {
            createItemUseCase = CreateItemUseCaseImpl(
                toDoItemRepository,
                CreateAlarmUseCaseImpl(applicationContext),
                ValidateItemUseCaseImpl()
            )
            updateItemUseCase = UpdateItemUseCaseImpl(
                toDoItemRepository,
                ValidateItemUseCaseImpl(),
                DeleteItemUseCaseImpl(
                    toDoItemRepository,
                    DeleteAlarmUseCaseImpl(applicationContext)
                ),
                CreateAlarmUseCaseImpl(applicationContext)
            )
        }
    }

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
            saveToDoItem()
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

    private fun saveToDoItem() {
        val newItem = ToDoItem(
            id = CreateItemUseCase.EMPTY_ID,
            text = etText.text.toString(),
            description = etDescription.text.toString(),
            reminder = reminder,
            color = CreateItemUseCase.EMPTY_COLOR
        )
        toDoItem.let { oldItem ->
            if (oldItem == null) {
                createItemUseCase.execute(newItem)
            } else {
                updateItemUseCase.execute(oldItem, newItem)
            }
        }
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
}