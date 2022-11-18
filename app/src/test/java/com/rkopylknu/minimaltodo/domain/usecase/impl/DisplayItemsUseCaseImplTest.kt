package com.rkopylknu.minimaltodo.domain.usecase.impl

import com.rkopylknu.minimaltodo.data.preferences.AppPreferences
import com.rkopylknu.minimaltodo.util.AppPreferencesManagerFake
import com.rkopylknu.minimaltodo.util.FakeData
import com.rkopylknu.minimaltodo.util.ToDoItemRepositoryFake
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class DisplayItemsUseCaseImplTest {

    @Test
    fun displays_items() {
        val useCase = DisplayItemsUseCaseImpl(
            ToDoItemRepositoryFake(FakeData.toDoItemList),
            AppPreferencesManagerFake()
        )

        val items = runBlocking {
            useCase.execute().first()
        }

        assertEquals(
            items.toSet(),
            FakeData.toDoItemList.toSet()
        )
    }

    @Test
    fun displays_empty_items() {
        val useCase = DisplayItemsUseCaseImpl(
            ToDoItemRepositoryFake(),
            AppPreferencesManagerFake()
        )

        val items = runBlocking {
            useCase.execute().first()
        }

        assert(items.isEmpty())
    }

    @Test
    fun displays_items_sorted_by_time() {
        val toDoItemRepository = ToDoItemRepositoryFake()
        val appPreferencesManager = AppPreferencesManagerFake()
        val useCase = DisplayItemsUseCaseImpl(
            toDoItemRepository,
            appPreferencesManager
        )

        val items = runBlocking {
            // Insert items in incorrect order
            toDoItemRepository.insert(FakeData.toDoItemList[1])
            toDoItemRepository.insert(FakeData.toDoItemList[0])
            toDoItemRepository.insert(FakeData.toDoItemList[2])

            appPreferencesManager.setSortOrder(
                AppPreferences.SortOrder.BY_TIME
            )
            useCase.execute().first()
        }

        assertEquals(items.size, 3)
        assert(
            items.windowed(2).all {
                val (a, b) = it
                a.id < b.id || a.isPrior >= b.isPrior
            }
        )
        assert(
            items.windowed(2).all { it[0].isPrior >= it[1].isPrior }
        )
    }

    @Test
    fun displays_items_sorted_by_name() {
        val toDoItemRepository = ToDoItemRepositoryFake()
        val appPreferencesManager = AppPreferencesManagerFake()
        val useCase = DisplayItemsUseCaseImpl(
            toDoItemRepository,
            appPreferencesManager
        )

        val items = runBlocking {
            // Insert items in incorrect order
            toDoItemRepository.insert(FakeData.toDoItemList[1])
            toDoItemRepository.insert(FakeData.toDoItemList[0])
            toDoItemRepository.insert(FakeData.toDoItemList[2])

            appPreferencesManager.setSortOrder(
                AppPreferences.SortOrder.BY_NAME
            )
            useCase.execute().first()
        }

        assertEquals(items.size, 3)
        assert(
            items.windowed(2).all {
                val (a, b) = it
                a.text < b.text || a.isPrior >= b.isPrior
            }
        )
        assert(
            items.windowed(2).all { it[0].isPrior >= it[1].isPrior }
        )
    }
}