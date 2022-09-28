package com.rkopylknu.minimaltodo.AddToDo

import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.AppDefault.AppDefaultActivity

class AddToDoActivity : AppDefaultActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_to_do)

        val crossDrawable = ResourcesCompat.getDrawable(
            resources,
            R.drawable.ic_clear_outlined,
            theme
        )?.apply {
            colorFilter = BlendModeColorFilterCompat
                .createBlendModeColorFilterCompat(
                    ResourcesCompat.getColor(resources, R.color.icons, theme),
                    BlendModeCompat.SRC_ATOP
                )
        }

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.run {
            elevation = 0f
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(crossDrawable)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return false
        }
        return true
    }

    companion object {

        const val TO_DO_ITEM_KEY = "to_do_item"
    }
}