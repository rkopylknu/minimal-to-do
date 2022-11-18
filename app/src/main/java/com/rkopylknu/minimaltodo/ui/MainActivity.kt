package com.rkopylknu.minimaltodo.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.rkopylknu.minimaltodo.NavGraphDirections
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.data.preferences.AppPreferencesManager
import com.rkopylknu.minimaltodo.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var _navController: NavController? = null
    private val navController get() = checkNotNull(_navController)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        _navController = (supportFragmentManager
            .findFragmentById(R.id.fragment_container_view) as NavHostFragment)
            .navController

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setupWithNavController(navController)

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
        runBlocking {
            val theme = appPreferencesManager.appPreferences.first().theme
            setTheme(theme)
        }
    }

    companion object {

        const val TO_DO_ITEM_KEY = "to_do_item"

        const val SHOW_REMINDER_ACTION =
            "com.rkopylknu.minimaltodo.ui.MainActivity.SHOW_REMINDER"
    }
}