package com.rkopylknu.minimaltodo.Reminder

import android.os.Bundle
import com.rkopylknu.minimaltodo.AppDefault.AppDefaultActivity
import com.rkopylknu.minimaltodo.R

class ReminderActivity : AppDefaultActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun contentViewLayoutRes() =
        R.layout.reminder_layout

    override fun createInitialFragment() =
        ReminderFragment.newInstance()
}