package com.rkopylknu.minimaltodo.Reminder

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.res.ResourcesCompat
import com.rkopylknu.minimaltodo.AppDefault.AppDefaultFragment
import com.rkopylknu.minimaltodo.Main.MainActivity
import com.rkopylknu.minimaltodo.Main.MainFragment
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.Utility.StoreRetrieveData
import com.rkopylknu.minimaltodo.Utility.ToDoItem
import com.rkopylknu.minimaltodo.Utility.TodoNotificationService
import org.json.JSONException
import java.io.IOException
import java.util.*

class ReminderFragment : AppDefaultFragment() {

    private lateinit var mtoDoTextTextView: TextView
    private lateinit var mRemoveToDoButton: Button
    private lateinit var mSnoozeSpinner: AppCompatSpinner
    private lateinit var snoozeOptionsArray: Array<String>
    private lateinit var mSnoozeTextView: TextView
    private lateinit var storeRetrieveData: StoreRetrieveData
    private var mToDoItems: ArrayList<ToDoItem?>? = null
    private var mItem: ToDoItem? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val theme = requireActivity()
            .getSharedPreferences(MainFragment.THEME_PREFERENCES, Context.MODE_PRIVATE)
            .getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME)

        if (theme == MainFragment.LIGHTTHEME) {
            activity?.setTheme(R.style.CustomStyle_LightTheme)
        } else {
            activity?.setTheme(R.style.CustomStyle_DarkTheme)
        }

        storeRetrieveData = StoreRetrieveData(context, MainFragment.FILENAME)
        mToDoItems = MainFragment.getLocallyStoredData(storeRetrieveData)

        val id = (activity as AppCompatActivity).run {
            setSupportActionBar(view.findViewById(R.id.toolbar))
            setHasOptionsMenu(true)

            if (Build.VERSION.SDK_INT >= 33) {
                intent.getSerializableExtra(TodoNotificationService.TODOUUID, UUID::class.java)
            } else {
                intent.getSerializableExtra(TodoNotificationService.TODOUUID) as UUID
            }
        }

        mItem = mToDoItems?.firstOrNull {
            it != null && it.identifier == id
        }

        snoozeOptionsArray = resources.getStringArray(R.array.snooze_options)

        mRemoveToDoButton = view.findViewById(R.id.toDoReminderRemoveButton)
        mtoDoTextTextView = view.findViewById(R.id.toDoReminderTextViewBody)
        mSnoozeTextView = view.findViewById(R.id.reminderViewSnoozeTextView)
        mSnoozeSpinner = view.findViewById(R.id.todoReminderSnoozeSpinner)

        mtoDoTextTextView.text = mItem?.toDoText
        if (theme == MainFragment.LIGHTTHEME) {
            mSnoozeTextView.setTextColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.secondary_text,
                    activity?.theme
                )
            )
        } else {
            mSnoozeTextView.setTextColor(Color.WHITE)
            mSnoozeTextView.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_snooze_white_24dp, 0, 0, 0
            )
        }

        mRemoveToDoButton.setOnClickListener {
            mToDoItems?.remove(mItem)
            changeOccurred()
            saveData()
            closeApp()
        }

        mSnoozeSpinner.adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_dropdown_item,
            snoozeOptionsArray
        )
    }

    override fun layoutRes() = R.layout.fragment_reminder

    private fun closeApp() {
        val sharedPreferences = requireActivity()
            .getSharedPreferences(
                MainFragment.SHARED_PREF_DATA_SET_CHANGED,
                Context.MODE_PRIVATE
            )
        sharedPreferences.edit()
            .putBoolean(EXIT, true)
            .apply()

        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_reminder, menu)
    }

    private fun changeOccurred() {
        val sharedPreferences = requireActivity()
            .getSharedPreferences(
                MainFragment.SHARED_PREF_DATA_SET_CHANGED,
                Context.MODE_PRIVATE
            )
        sharedPreferences.edit()
            .putBoolean(MainFragment.CHANGE_OCCURED, true)
            .apply()
    }

    private fun addTimeToDate(mins: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.MINUTE, mins)
        return calendar.time
    }

    private fun valueFromSpinner(): Int {
        return when (mSnoozeSpinner.selectedItemPosition) {
            0 -> 10
            1 -> 30
            2 -> 60
            else -> 0
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toDoReminderDoneMenuItem -> {
                val date = addTimeToDate(valueFromSpinner())
                mItem?.toDoDate = date
                mItem?.setHasReminder(true)
                changeOccurred()
                saveData()
                closeApp()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun saveData() {
        storeRetrieveData.saveToFile(mToDoItems)
    }

    companion object {

        const val EXIT = "com.avjindersekhon.exit"

        fun newInstance() = ReminderFragment()
    }
}