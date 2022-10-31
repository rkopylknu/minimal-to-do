package com.rkopylknu.minimaltodo.domain.usecase.impl

import com.rkopylknu.minimaltodo.data.preferences.AppPreferences
import com.rkopylknu.minimaltodo.data.preferences.AppPreferencesManager
import com.rkopylknu.minimaltodo.data.repository.ToDoItemRepository
import com.rkopylknu.minimaltodo.domain.model.ToDoItem
import com.rkopylknu.minimaltodo.domain.usecase.DisplayItemsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DisplayItemsUseCaseImpl @Inject constructor(
    private val toDoItemRepository: ToDoItemRepository,
    private val appPreferencesManager: AppPreferencesManager,
) : DisplayItemsUseCase {

    override fun execute(): Flow<List<ToDoItem>> =
        toDoItemRepository.getAll().map { toDoItems ->
            val preferences = appPreferencesManager.appPreferences.first()
            when (preferences.sortOrder) {
                AppPreferences.SortOrder.BY_TIME ->
                    toDoItems.sortedBy { it.id }
                AppPreferences.SortOrder.BY_NAME ->
                    toDoItems.sortedBy { it.text }
            }.sortedBy { !it.isPrior }
        }
}