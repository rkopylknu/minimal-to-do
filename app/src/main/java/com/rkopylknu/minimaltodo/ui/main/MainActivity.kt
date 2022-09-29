package com.rkopylknu.minimaltodo.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.ui.about.AboutActivity
import com.rkopylknu.minimaltodo.ui.default.AppDefaultActivity
import com.rkopylknu.minimaltodo.ui.settings.SettingsActivity

class MainActivity : AppDefaultActivity() {

    private val settingsActivityLauncher =
        registerForActivityResult(StartActivityForResult()) {
            // Apply new settings
            recreate()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_about_me -> {
                val startAboutActivityIntent = Intent(
                    this, AboutActivity::class.java
                )
                startActivity(startAboutActivityIntent)
            }
            R.id.item_preferences -> {
                settingsActivityLauncher.launch(
                    Intent(this, SettingsActivity::class.java)
                )
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}