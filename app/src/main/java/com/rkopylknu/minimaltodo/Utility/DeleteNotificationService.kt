package com.rkopylknu.minimaltodo.Utility

import android.app.IntentService
import android.content.Intent
import com.rkopylknu.minimaltodo.Main.MainFragment
import android.os.Build
import java.lang.Exception
import java.util.*

class DeleteNotificationService : IntentService("DeleteNotificationService") {

    private lateinit var storeRetrieveData: StoreRetrieveData
    private var mToDoItems: ArrayList<ToDoItem?>? = null

    override fun onHandleIntent(intent: Intent?) {
        storeRetrieveData = StoreRetrieveData(this, MainFragment.FILENAME)
        val todoID = if (Build.VERSION.SDK_INT >= 33) {
            intent!!.getSerializableExtra(
                TodoNotificationService.TODOUUID,
                UUID::class.java
            )
        } else {
            intent!!.getSerializableExtra(TodoNotificationService.TODOUUID) as UUID?
        }

        mToDoItems = loadData()
        val toDoItem = mToDoItems?.firstOrNull { item ->
            item != null && item.id == todoID
        }
        if (toDoItem != null) {
            mToDoItems?.remove(toDoItem)
            dataChanged()
            saveData()
        }
    }

    private fun dataChanged() {
        val sharedPreferences =
            getSharedPreferences(MainFragment.SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(MainFragment.CHANGE_OCCURED, true)
        editor.apply()
    }

    private fun saveData() {
        try {
            storeRetrieveData.saveToFile(mToDoItems)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        saveData()
    }

    private fun loadData(): ArrayList<ToDoItem?>? {
        try {
            return storeRetrieveData.loadFromFile()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}