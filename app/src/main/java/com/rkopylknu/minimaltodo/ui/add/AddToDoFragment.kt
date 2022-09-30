package com.rkopylknu.minimaltodo.ui.add

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rkopylknu.minimaltodo.App
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.domain.usecase.impl.*
import com.rkopylknu.minimaltodo.util.appCompatActivity
import com.rkopylknu.minimaltodo.util.getColorCompat
import com.rkopylknu.minimaltodo.util.getDrawableCompat
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

class AddToDoFragment(
    private val toDoItem: ToDoItem? = null,
) : Fragment(R.layout.fragment_add_to_do) {

    private val viewModel: AddToDoViewModel by viewModels {
        (requireActivity().application as App).run {
            AddToDoViewModel.Factory(
                CreateItemUseCaseImpl(
                    toDoItemRepository,
                    CreateAlarmUseCaseImpl(applicationContext),
                    ValidateItemUseCaseImpl()
                ),
                UpdateItemUseCaseImpl(
                    toDoItemRepository,
                    ValidateItemUseCaseImpl(),
                    DeleteItemUseCaseImpl(
                        toDoItemRepository,
                        DeleteAlarmUseCaseImpl(applicationContext)
                    ),
                    CreateAlarmUseCaseImpl(applicationContext)
                ),
                toDoItem
            )
        }
    }

    private lateinit var etText: EditText
    private lateinit var etDescription: EditText
    private lateinit var switchAddReminder: SwitchCompat
    private lateinit var etReminderDate: EditText
    private lateinit var etReminderTime: EditText
    private lateinit var fabSaveToDoItem: FloatingActionButton
    private lateinit var btnCopy: Button
    private lateinit var clReminder: ConstraintLayout
    private lateinit var tvReminderSet: TextView

    private var actionBarElevation: Float? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appCompatActivity?.supportActionBar?.run {
            val crossIcon = getDrawableCompat(R.drawable.ic_clear_outlined)
                ?.applyIconsColorFilter()
            setHomeAsUpIndicator(crossIcon)

            actionBarElevation = elevation
            elevation = 0f
        }

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

        viewModel.toDoItem?.run {
            etText.setText(text)
            etDescription.setText(description)
            displayReminder()
        }

        switchAddReminder.run {
            isChecked = viewModel.reminder != null
            setOnClickListener {
                if (switchAddReminder.isChecked) {
                    if (viewModel.reminder == null) {
                        viewModel.setDefaultReminder()
                    }
                } else {
                    viewModel.resetReminder()
                }
                displayReminder()
            }
        }

        fabSaveToDoItem.setOnClickListener {
            viewModel.onSaveItem(
                etText.text.toString(),
                etDescription.text.toString()
            )
            parentFragmentManager.popBackStack()
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

    private fun displayReminder() {
        val reminder = viewModel.reminder
        etReminderDate.setText(reminder?.date?.toString() ?: "")
        etReminderTime.setText(reminder?.time?.toString() ?: "")
        tvReminderSet.text = reminder?.let {
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
            viewModel.onDateSet(year, month, day)
            displayReminder()
        }

        override fun onTimeSet(picker: TimePicker?, hour: Int, minute: Int) {
            viewModel.onTimeSet(hour, minute)
            displayReminder()
        }
    }

    private fun copyToDoItemToClipboard() {
        val clipboard = requireActivity()
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        clipboard.setPrimaryClip(
            ClipData.newPlainText(
                "text",
                viewModel.getClipboardText(
                    etText.text.toString(),
                    etDescription.text.toString()
                )
            )
        )

        Toast.makeText(
            context,
            "Copied To Clipboard!",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun Drawable.applyIconsColorFilter() = apply {
        colorFilter = BlendModeColorFilterCompat
            .createBlendModeColorFilterCompat(
                ResourcesCompat.getColor(
                    resources, R.color.icons, requireActivity().theme
                ),
                BlendModeCompat.SRC_ATOP
            )
    }

    override fun onDestroyView() {
        super.onDestroyView()

        appCompatActivity?.supportActionBar?.run {
            val backIcon = getDrawableCompat(R.drawable.ic_back_outlined)
                ?.applyIconsColorFilter()
            setHomeAsUpIndicator(backIcon)

            actionBarElevation?.let { elevation = it }
        }
    }
}