package com.rkopylknu.minimaltodo.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rkopylknu.minimaltodo.data.preferences.AppPreferencesManager
import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.domain.usecase.DeleteItemUseCase
import com.rkopylknu.minimaltodo.domain.usecase.DisplayItemsUseCase
import com.rkopylknu.minimaltodo.domain.usecase.ReplaceItemUseCase
import com.rkopylknu.minimaltodo.domain.usecase.RestoreItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val displayItemsUseCase: DisplayItemsUseCase,
    private val replaceItemUseCase: ReplaceItemUseCase,
    private val deleteItemUseCase: DeleteItemUseCase,
    private val restoreItemUseCase: RestoreItemUseCase,
    private val appPreferencesManager: AppPreferencesManager
) : ViewModel() {

    val toDoItems = displayItemsUseCase.execute()

    val theme get() = appPreferencesManager.get().theme

    private var lastDeletedItem: ToDoItem? = null
    private var lastDeletedIndex: Int? = null

    fun onReplaceItem(from: Int, to: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            replaceItemUseCase.execute(from, to)
        }
    }

    fun onDeleteItem(item: ToDoItem, index: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteItemUseCase.execute(item.id)
            lastDeletedItem = item
            lastDeletedIndex = index
        }
    }

    fun onRestoreItem() {
        val item = lastDeletedItem ?: return
        val index = lastDeletedIndex ?: return
        viewModelScope.launch {
            restoreItemUseCase.execute(item, index)
        }
    }
}