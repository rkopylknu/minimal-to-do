package com.rkopylknu.minimaltodo

import android.app.Application
import com.rkopylknu.minimaltodo.data.preferences.AppPreferencesManager
import com.rkopylknu.minimaltodo.data.preferences.AppPreferencesManagerImpl
import com.rkopylknu.minimaltodo.data.storage.StoreRetrieveData
import com.rkopylknu.minimaltodo.data.storage.StoreRetrieveDataImpl
import com.rkopylknu.minimaltodo.data.repository.ToDoItemRepository
import com.rkopylknu.minimaltodo.data.repository.ToDoItemRepositoryImpl
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application()