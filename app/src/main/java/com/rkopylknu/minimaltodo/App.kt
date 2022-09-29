package com.rkopylknu.minimaltodo

import android.app.Application
import com.rkopylknu.minimaltodo.data.preferences.AppPreferencesManager
import com.rkopylknu.minimaltodo.data.preferences.AppPreferencesManagerImpl
import com.rkopylknu.minimaltodo.data.storage.StoreRetrieveData
import com.rkopylknu.minimaltodo.data.storage.StoreRetrieveDataImpl
import com.rkopylknu.minimaltodo.data.repository.ToDoItemRepository
import com.rkopylknu.minimaltodo.data.repository.ToDoItemRepositoryImpl

class App : Application() {

    private var _toDoItemRepository: ToDoItemRepository? = null
    val toDoItemRepository
        get() = checkNotNull(_toDoItemRepository)

    private var _appPreferencesManager: AppPreferencesManager? = null
    val appPreferencesManager
        get() = checkNotNull(_appPreferencesManager)

    override fun onCreate() {
        super.onCreate()

        val storeRetrieveData: StoreRetrieveData =
            StoreRetrieveDataImpl(applicationContext)

        _toDoItemRepository =
            ToDoItemRepositoryImpl(storeRetrieveData)
        _appPreferencesManager =
            AppPreferencesManagerImpl(applicationContext)
    }
}