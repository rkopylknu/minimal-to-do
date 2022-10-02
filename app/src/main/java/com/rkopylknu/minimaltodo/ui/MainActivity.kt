package com.rkopylknu.minimaltodo.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.rkopylknu.minimaltodo.NavGraphDirections
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.data.preferences.AppPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _navController: NavController? = null
    private val navController get() = checkNotNull(_navController)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        _navController = (supportFragmentManager
            .findFragmentById(R.id.fragment_container_view) as NavHostFragment)
            .navController

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            onDestinationChanged(destination)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent?.action == SHOW_REMINDER_ACTION) {
            tryShowReminder(intent)
        }
    }

    override fun onSupportNavigateUp() =
        navController.navigateUp() || super.onSupportNavigateUp()

    private fun tryShowReminder(intent: Intent) {
        val toDoItemJson = intent.getStringExtra(TO_DO_ITEM_KEY)

        check(toDoItemJson != null) {
            "Can't show reminder without ToDoItem"
        }

        navController.navigate(
            NavGraphDirections.actionGlobalReminderFragment(toDoItemJson)
        )
    }

    private fun onDestinationChanged(destination: NavDestination) {
        supportActionBar?.run {
            setDisplayShowHomeEnabled(destination.id == R.id.mainFragment)
        }
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.isVisible = destination.id != R.id.addToDoFragment
    }

    @Inject
    fun applyTheme(appPreferencesManager: AppPreferencesManager) {
        setTheme(appPreferencesManager.get().theme)
    }

    companion object {

        const val TO_DO_ITEM_KEY = "to_do_item"

        const val SHOW_REMINDER_ACTION =
            "com.rkopylknu.minimaltodo.ui.MainActivity.SHOW_REMINDER"
    }
}