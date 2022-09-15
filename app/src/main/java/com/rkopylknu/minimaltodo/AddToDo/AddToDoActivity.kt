package com.rkopylknu.minimaltodo.AddToDo

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.rkopylknu.minimaltodo.AppDefault.AppDefaultActivity
import com.rkopylknu.minimaltodo.R

class AddToDoActivity : AppDefaultActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun contentViewLayoutRes() =
        R.layout.activity_add_to_do

    override fun createInitialFragment(): Fragment =
        AddToDoFragment.newInstance()
}