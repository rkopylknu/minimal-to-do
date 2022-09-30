package com.rkopylknu.minimaltodo.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rkopylknu.minimaltodo.data.preferences.AppPreferencesManager
import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.domain.usecase.DeleteItemUseCase
import com.rkopylknu.minimaltodo.domain.usecase.DisplayItemsUseCase
import com.rkopylknu.minimaltodo.domain.usecase.ReplaceItemUseCase
import com.rkopylknu.minimaltodo.domain.usecase.RestoreItemUseCase

class MainViewModel(
    private val displayItemsUseCase: DisplayItemsUseCase,
    private val replaceItemUseCase: ReplaceItemUseCase,
    private val deleteItemUseCase: DeleteItemUseCase,
    private val restoreItemUseCase: RestoreItemUseCase,
    private val appPreferencesManager: AppPreferencesManager
) : ViewModel() {

    val toDoItems get() = displayItemsUseCase.execute()

    val theme get() = appPreferencesManager.get().theme

    private var lastDeletedItem: ToDoItem? = null
    private var lastDeletedIndex: Int? = null

    fun onReplaceItem(from: Int, to: Int) {
        replaceItemUseCase.execute(from, to)
    }

    fun onDeleteItem(item: ToDoItem, index: Int) {
        deleteItemUseCase.execute(item)
        lastDeletedItem = item
        lastDeletedIndex = index
    }

    fun onRestoreItem() {
        val item = lastDeletedItem ?: return
        val index = lastDeletedIndex ?: return
        restoreItemUseCase.execute(item, index)
    }

    class Factory(
        private val displayItemsUseCase: DisplayItemsUseCase,
        private val replaceItemUseCase: ReplaceItemUseCase,
        private val deleteItemUseCase: DeleteItemUseCase,
        private val restoreItemUseCase: RestoreItemUseCase,
        private val appPreferencesManager: AppPreferencesManager
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(
                    displayItemsUseCase,
                    replaceItemUseCase,
                    deleteItemUseCase,
                    restoreItemUseCase,
                    appPreferencesManager
                ) as T
            } else {
                throw IllegalArgumentException("Unexpected ViewModel class")
            }
        }
    }
}