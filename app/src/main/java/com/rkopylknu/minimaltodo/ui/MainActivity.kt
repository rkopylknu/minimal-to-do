package com.rkopylknu.minimaltodo.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.rkopylknu.minimaltodo.App
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.ui.main.MainFragment
import com.rkopylknu.minimaltodo.ui.reminder.ReminderFragment
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val appPreferencesManager =
            (application as App).appPreferencesManager

        setTheme(appPreferencesManager.get().theme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(
            supportFragmentManager.fragments.last() !is MainFragment
        )

        supportFragmentManager.addOnBackStackChangedListener {
            val isHome = supportFragmentManager
                .fragments.last() is MainFragment
            supportActionBar?.setDisplayHomeAsUpEnabled(!isHome)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent?.action == SHOW_REMINDER_ACTION) {
            tryShowReminder(intent)
        }
    }

    private fun tryShowReminder(intent: Intent) {
        val toDoItem = intent.getStringExtra(TO_DO_ITEM_KEY)
            ?.let { Json.decodeFromString<ToDoItem>(it) }

        check(toDoItem != null) {
            "Can't show reminder without ToDoItem"
        }
        supportFragmentManager.run {
            // Clear back stack
            popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

            commit {
                setReorderingAllowed(true)
                replace(R.id.fragment_container_view, ReminderFragment(toDoItem))
                addToBackStack(null)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        supportFragmentManager.popBackStack()
        return true
    }

    companion object {

        const val TO_DO_ITEM_KEY = "to_do_item"

        const val SHOW_REMINDER_ACTION =
            "com.rkopylknu.minimaltodo.ui.MainActivity.SHOW_REMINDER"
    }
}