package com.rkopylknu.minimaltodo.Settings

import android.app.FragmentManager
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.rkopylknu.minimaltodo.Main.MainFragment
import com.rkopylknu.minimaltodo.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val theme = getSharedPreferences(
            MainFragment.THEME_PREFERENCES,
            Context.MODE_PRIVATE
        ).getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME)

        if (theme == MainFragment.LIGHTTHEME) {
            setTheme(R.style.CustomStyle_LightTheme)
        } else {
            setTheme(R.style.CustomStyle_DarkTheme)
        }

        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val backArrow = ResourcesCompat.getDrawable(
            resources,
            R.drawable.abc_ic_ab_back_mtrl_am_alpha,
            this@SettingsActivity.theme
        )?.apply {
            colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                Color.WHITE,
                BlendModeCompat.SRC_ATOP
            )
        }

        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(backArrow)
        }

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.mycontent, SettingsFragment())
            commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                if (NavUtils.getParentActivityName(this) != null) {
                    NavUtils.navigateUpFromSameTask(this)
                }
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}