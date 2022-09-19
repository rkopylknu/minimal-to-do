package com.rkopylknu.minimaltodo.AddToDo

import android.animation.Animator
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.setFragmentResult
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rkopylknu.minimaltodo.AppDefault.AppDefaultFragment
import com.rkopylknu.minimaltodo.Main.MainFragment
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.Utility.ToDoItem
import java.text.SimpleDateFormat
import java.util.*

class AddToDoFragment : AppDefaultFragment(),
    DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private lateinit var toDoTextBodyEditText: EditText
    private lateinit var toDoTextBodyDescription: EditText
    private lateinit var toDoDateSwitch: SwitchCompat
    private lateinit var userDateSpinnerContainingLinearLayout: LinearLayout
    private lateinit var reminderTextView: TextView
    private lateinit var combinationText: String
    private lateinit var mDateEditText: EditText
    private lateinit var mTimeEditText: EditText
    private lateinit var copyClipboard: Button
    private lateinit var userToDoItem: ToDoItem
    private lateinit var toDoSendFloatingActionButton: FloatingActionButton
    private lateinit var containerLayout: LinearLayout
    private lateinit var userSelectedText: String
    private var userEnteredDescription: String = ""
    private var userHasReminder = false
    private var userReminderDate: Date? = null
    private var userColor = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val theme = activity
            ?.getSharedPreferences(
                MainFragment.THEME_PREFERENCES,
                Context.MODE_PRIVATE
            )
            ?.getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME)

        activity?.setTheme(
            if (theme == MainFragment.LIGHTTHEME) {
                R.style.CustomStyle_LightTheme
            } else {
                R.style.CustomStyle_DarkTheme
            }
        )

        val cross = ResourcesCompat.getDrawable(
            resources,
            R.drawable.ic_clear_white_24dp,
            activity?.theme
        )?.apply {
            colorFilter = BlendModeColorFilterCompat
                .createBlendModeColorFilterCompat(
                    ResourcesCompat.getColor(resources, R.color.icons, activity?.theme),
                    BlendModeCompat.SRC_ATOP
                )
        }

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (activity as? AppCompatActivity)?.run {
            setSupportActionBar(toolbar)
            supportActionBar?.run {
                elevation = 0f
                setDisplayShowTitleEnabled(false)
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(cross)
            }
        }

        userToDoItem = if (Build.VERSION.SDK_INT >= 33) {
            requireActivity().intent!!
                .getSerializableExtra(MainFragment.TODOITEM, ToDoItem::class.java)!!
        } else {
            requireActivity().intent!!
                .getSerializableExtra(MainFragment.TODOITEM) as ToDoItem
        }
        userToDoItem.run {
            userSelectedText = text
            userEnteredDescription = description
            userHasReminder = hasReminder
            userReminderDate = date
            userColor = color
        }

        val reminderIconImageButton =
            view.findViewById<ImageButton>(R.id.userToDoReminderIconImageButton)
        val reminderRemindMeTextView =
            view.findViewById<TextView>(R.id.userToDoRemindMeTextView)

        if (theme == MainFragment.DARKTHEME) {
            reminderIconImageButton
                .setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_alarm_add_white_24dp,
                        activity?.theme
                    )
                )
            reminderRemindMeTextView.setTextColor(Color.WHITE)
        }

        copyClipboard = view.findViewById(R.id.copyclipboard)
        containerLayout =
            view.findViewById(R.id.todoReminderAndDateContainerLayout)
        userDateSpinnerContainingLinearLayout =
            view.findViewById(R.id.toDoEnterDateLinearLayout)
        toDoTextBodyEditText = view.findViewById(R.id.userToDoEditText)
        toDoTextBodyDescription = view.findViewById(R.id.userToDoDescription)
        toDoDateSwitch = view.findViewById(R.id.toDoHasDateSwitchCompat)
        toDoSendFloatingActionButton =
            view.findViewById(R.id.makeToDoFloatingActionButton)
        reminderTextView =
            view.findViewById(R.id.newToDoDateTimeReminderTextView)

        copyClipboard.setOnClickListener {
            val toDoTextContainer = toDoTextBodyEditText.text
            val toDoTextBodyDescriptionContainer =
                toDoTextBodyDescription.text
            val clipboard = activity

                ?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            combinationText = "Title : $toDoTextContainer\n" +
                    "Description : $toDoTextBodyDescriptionContainer\n" +
                    " -Copied From MinimalToDo"
            val clip: ClipData = ClipData.newPlainText("text", combinationText)

            clipboard?.setPrimaryClip(clip)
            Toast.makeText(
                context,
                "Copied To Clipboard!",
                Toast.LENGTH_SHORT
            ).show()
        }

        containerLayout.setOnClickListener {
            hideKeyboard(toDoTextBodyEditText)
            hideKeyboard(toDoTextBodyDescription)
        }

        if (userHasReminder && userReminderDate != null) {
            setReminderTextView()
            setEnterDateLayoutVisibleWithAnimations(true)
        }
        if (userReminderDate == null) {
            toDoDateSwitch.isChecked = false
            reminderTextView.visibility = View.INVISIBLE
        }

        toDoTextBodyEditText.requestFocus()
        toDoTextBodyEditText.setText(userSelectedText)
        toDoTextBodyDescription.setText(userEnteredDescription)

        val inputMethodManager =
            activity?.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager

        toDoTextBodyEditText.run {
            setSelection(length())
            inputMethodManager.showSoftInput(this, 0)
            addTextChangedListener(object : TextWatcher {

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    userSelectedText = s.toString()
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(s: Editable) {}
            })

            setText(userEnteredDescription)
            setSelection(length())
        }

        setEnterDateLayoutVisible(toDoDateSwitch.isChecked)

        toDoDateSwitch.isChecked = userHasReminder && userReminderDate != null
        toDoDateSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                userReminderDate = null
            }
            userHasReminder = isChecked

            setDateAndTimeEditText()
            setEnterDateLayoutVisibleWithAnimations(isChecked)

            hideKeyboard(toDoTextBodyEditText)
            hideKeyboard(toDoTextBodyDescription)
        }

        toDoSendFloatingActionButton.setOnClickListener {
            toDoTextBodyEditText.run {
                if (length() <= 0) {
                    setError(getString(R.string.todo_error))
                } else if (
                    userReminderDate != null &&
                    userReminderDate!!.before(Date())
                ) {
                    makeResult(Activity.RESULT_CANCELED)
                } else {
                    makeResult(Activity.RESULT_OK)
                    activity?.finish()
                }
            }
            hideKeyboard(toDoTextBodyEditText)
            hideKeyboard(toDoTextBodyDescription)
        }

        mDateEditText = view.findViewById<EditText>(R.id.newTodoDateEditText).apply {
            setOnClickListener {
                hideKeyboard(toDoTextBodyEditText)
                val date = userReminderDate
                val calendar = Calendar.getInstance()
                calendar.time = date
                val year = calendar[Calendar.YEAR]
                val month = calendar[Calendar.MONTH]
                val day = calendar[Calendar.DAY_OF_MONTH]

                DatePickerDialog(
                    context, this@AddToDoFragment,
                    year, month, day
                ).show()
            }
        }

        mTimeEditText = view.findViewById<EditText>(R.id.newTodoTimeEditText).apply {
            setOnClickListener {
                hideKeyboard(toDoTextBodyEditText)
                val date = userReminderDate
                val calendar = Calendar.getInstance()
                calendar.time = date
                val hour = calendar[Calendar.HOUR_OF_DAY]
                val minute = calendar[Calendar.MINUTE]

                TimePickerDialog(
                    context, this@AddToDoFragment,
                    hour, minute,
                    DateFormat.is24HourFormat(context)
                ).show()
            }
        }

        setDateAndTimeEditText()
    }

    private fun setDateAndTimeEditText() {
        if (userToDoItem.hasReminder && userReminderDate != null) {
            val userDate = formatDate("d MMM, yyyy", userReminderDate)
            val formatToUse = if (DateFormat.is24HourFormat(context)) {
                "k:mm"
            } else {
                "h:mm a"
            }
            val userTime = formatDate(formatToUse, userReminderDate)
            mTimeEditText.setText(userTime)
            mDateEditText.setText(userDate)
        } else {
            mDateEditText.setText(getString(R.string.date_reminder_default))
            val time24 = DateFormat.is24HourFormat(context)
            val cal = Calendar.getInstance()
            if (time24) {
                cal[Calendar.HOUR_OF_DAY] = cal[Calendar.HOUR_OF_DAY] + 1
            } else {
                cal[Calendar.HOUR] = cal[Calendar.HOUR] + 1
            }
            cal[Calendar.MINUTE] = 0
            userReminderDate = cal.time
            val timeString: String = if (time24) {
                formatDate("k:mm", userReminderDate)
            } else {
                formatDate("h:mm a", userReminderDate)
            }
            mTimeEditText.setText(timeString)
        }
    }

    private val themeSet: String
        get() = requireActivity()
            .getSharedPreferences(
                MainFragment.THEME_PREFERENCES,
                Context.MODE_PRIVATE
            )
            .getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME)!!

    private fun hideKeyboard(et: EditText?) {
        val inputMethodManager = activity
            ?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(et?.windowToken, 0)
    }

    private fun setDate(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        val hour: Int
        val minute: Int

        val reminderCalendar = Calendar.getInstance()
        reminderCalendar[year, month] = day

        if (reminderCalendar.before(calendar)) return
        if (userReminderDate != null) {
            calendar.time = userReminderDate
        }

        hour = if (DateFormat.is24HourFormat(context)) {
            calendar[Calendar.HOUR_OF_DAY]
        } else {
            calendar[Calendar.HOUR]
        }
        minute = calendar[Calendar.MINUTE]
        calendar[year, month, day, hour] = minute

        userReminderDate = calendar.time

        setReminderTextView()
        setDateEditText()
    }

    private fun setTime(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        if (userReminderDate != null) {
            calendar.time = userReminderDate
        }

        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        calendar[year, month, day, hour, minute] = 0

        userReminderDate = calendar.time

        setReminderTextView()
        setTimeEditText()
    }

    private fun setDateEditText() {
        val dateFormat = "d MMM, yyyy"
        mDateEditText.setText(formatDate(dateFormat, userReminderDate))
    }

    private fun setTimeEditText() {
        val dateFormat: String =
            if (DateFormat.is24HourFormat(context)) "k:mm"
            else "h:mm a"
        mTimeEditText.setText(formatDate(dateFormat, userReminderDate))
    }

    private fun setReminderTextView() {
        if (userReminderDate != null) {
            reminderTextView.visibility = View.INVISIBLE
            return
        }

        reminderTextView.visibility = View.VISIBLE

        if (userReminderDate!!.before(Date())) {
            reminderTextView.text = getString(R.string.date_error_check_again)
            reminderTextView.setTextColor(Color.RED)
            return
        }

        val date: Date = userReminderDate!!
        val dateString = formatDate("d MMM, yyyy", date)

        val timeString: String
        var amPmString = ""

        if (DateFormat.is24HourFormat(context)) {
            timeString = formatDate("k:mm", date)
        } else {
            timeString = formatDate("h:mm", date)
            amPmString = formatDate("a", date)
        }

        val finalString =
            String.format(
                getString(R.string.remind_date_and_time),
                dateString, timeString, amPmString
            )

        reminderTextView.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.secondary_text,
                activity?.theme
            )
        )
        reminderTextView.text = finalString
    }

    private fun makeResult(result: Int) {
        val intent = Intent()

        if (userSelectedText.isNotEmpty()) {
            val capitalizedString =
                userSelectedText[0]
                    .uppercaseChar().toString() +
                        userSelectedText.substring(1)

            userToDoItem = userToDoItem.copy(
                text = capitalizedString,
                description = userEnteredDescription
            )
        } else {
            userToDoItem = userToDoItem.copy(
                text = userSelectedText,
                description = userEnteredDescription
            )
        }

        val calendar = Calendar.getInstance()
        calendar.time = userReminderDate
        calendar[Calendar.SECOND] = 0
        userReminderDate = calendar.time

        userToDoItem = userToDoItem.copy(
            hasReminder = userHasReminder,
            date = userReminderDate!!,
            color = userColor
        )

        intent.putExtra(MainFragment.TODOITEM, userToDoItem)
        activity?.setResult(result, intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                if (NavUtils.getParentActivityName(requireActivity()) != null) {
                    makeResult(Activity.RESULT_CANCELED)
                    NavUtils.navigateUpFromSameTask(requireActivity())
                }
                hideKeyboard(toDoTextBodyEditText)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDateSet(datePicker: DatePicker, year: Int, month: Int, day: Int) {
        setDate(year, month, day)
    }

    override fun onTimeSet(timePicker: TimePicker, hour: Int, minute: Int) {
        setTime(hour, minute)
    }

    private fun setEnterDateLayoutVisible(checked: Boolean) {
        userDateSpinnerContainingLinearLayout.visibility =
            if (checked) View.VISIBLE else View.INVISIBLE
    }

    private fun setEnterDateLayoutVisibleWithAnimations(checked: Boolean) {
        if (checked) {
            setReminderTextView()
        }
        userDateSpinnerContainingLinearLayout.animate()
            .alpha(if (checked) 1f else 0f)
            .setDuration(500)
            .setListener(
                object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                        userDateSpinnerContainingLinearLayout.visibility =
                            if (checked) View.VISIBLE else View.INVISIBLE
                    }

                    override fun onAnimationEnd(animation: Animator) {}
                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                }
            )
    }

    override fun layoutRes() = R.layout.fragment_add_to_do

    companion object {

        fun formatDate(formatString: String?, dateToFormat: Date?): String {
            val simpleDateFormat = SimpleDateFormat(formatString)
            return simpleDateFormat.format(dateToFormat)
        }

        fun newInstance(): AddToDoFragment {
            return AddToDoFragment()
        }
    }
}