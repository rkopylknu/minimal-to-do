package com.rkopylknu.minimaltodo.Reminder

import android.os.Bundle
import com.rkopylknu.minimaltodo.R
import com.rkopylknu.minimaltodo.AppDefault.AppDefaultActivity

class ReminderActivity : AppDefaultActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        setSupportActionBar(findViewById(R.id.toolbar))
    }

    companion object {

        const val TO_DO_ITEM_KEY = "to_do_item"
    }
}