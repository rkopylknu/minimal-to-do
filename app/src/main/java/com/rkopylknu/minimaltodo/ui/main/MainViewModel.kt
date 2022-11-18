package com.rkopylknu.minimaltodo.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkopylknu.minimaltodo.data.preferences.AppPreferences
import com.rkopylknu.minimaltodo.data.preferences.AppPreferencesManager
import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.domain.usecase.DeleteItemUseCase
import com.rkopylknu.minimaltodo.domain.usecase.DisplayItemsUseCase
import com.rkopylknu.minimaltodo.domain.usecase.RestoreItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    displayItemsUseCase: DisplayItemsUseCase,
    private val deleteItemUseCase: DeleteItemUseCase,
    private val restoreItemUseCase: RestoreItemUseCase,
    appPreferencesManager: AppPreferencesManager,
) : ViewModel() {

    val toDoItems = displayItemsUseCase.execute()

    private var lastDeletedItem: ToDoItem? = null
    private var lastDeletedIndex: Int? = null

    val theme =
        appPreferencesManager.appPreferences.map { it.theme }

    fun onDeleteItem(item: ToDoItem, index: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteItemUseCase.execute(item.id)
            lastDeletedItem = item
            lastDeletedIndex = index
        }
    }

    fun onRestoreItem() {
        val item = lastDeletedItem ?: return
        viewModelScope.launch {
            restoreItemUseCase.execute(item)
        }
    }
}