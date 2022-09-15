package com.rkopylknu.minimaltodo.About

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager.PackageInfoFlags
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.rkopylknu.minimaltodo.AppDefault.AppDefaultActivity
import com.rkopylknu.minimaltodo.Main.MainFragment
import com.rkopylknu.minimaltodo.R

class AboutActivity : AppDefaultActivity() {

    var appVersion = "0.1"
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val theme = getSharedPreferences(MainFragment.THEME_PREFERENCES, Context.MODE_PRIVATE)
            .getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME)

        application?.setTheme(
            if (theme == MainFragment.DARKTHEME) {
                R.style.CustomStyle_DarkTheme
            } else {
                R.style.CustomStyle_LightTheme
            }
        )

        val backArrow = ResourcesCompat.getDrawable(
            resources,
            R.drawable.abc_ic_ab_back_mtrl_am_alpha,
            this.theme
        )?.apply {
            colorFilter = BlendModeColorFilterCompat
                .createBlendModeColorFilterCompat(Color.WHITE, BlendModeCompat.SRC_ATOP)
        }

        val info: PackageInfo =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager
                    .getPackageInfo(packageName, PackageInfoFlags.of(0L))
            } else {
                packageManager.getPackageInfo(packageName, 0)
            }
        appVersion = info.versionName

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(backArrow)
        }
    }

    override fun contentViewLayoutRes() = R.layout.about_layout

    override fun createInitialFragment() = AboutFragment.newInstance()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                if (NavUtils.getParentActivityName(this) != null) {
                    NavUtils.navigateUpFromSameTask(this)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}