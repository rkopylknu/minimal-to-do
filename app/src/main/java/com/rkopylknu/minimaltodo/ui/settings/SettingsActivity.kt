package com.rkopylknu.minimaltodo.ui.settings

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.ui.default.AppDefaultActivity

class SettingsActivity : AppDefaultActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backArrow = ResourcesCompat.getDrawable(
            resources,
            R.drawable.ic_back_outlined,
            theme
        )?.apply {
            colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                Color.WHITE,
                BlendModeCompat.SRC_ATOP
            )
        }

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(backArrow)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return false
        }
        return true
    }
}