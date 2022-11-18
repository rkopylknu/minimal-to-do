package com.rkopylknu.minimaltodo.domain.usecase.impl

import com.rkopylknu.minimaltodo.util.CreateAlarmUseCaseFake
import com.rkopylknu.minimaltodo.util.FakeData
import com.rkopylknu.minimaltodo.util.ToDoItemRepositoryFake
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class UpdateItemUseCaseImplTest {

    @Test
    fun updates_valid_item() {
        val toDoItemRepository = ToDoItemRepositoryFake()
        val createAlarmUseCaseFake = CreateAlarmUseCaseFake()
        val useCase = UpdateItemUseCaseImpl(
            toDoItemRepository,
            ValidateItemUseCaseImpl(),
            createAlarmUseCaseFake
        )

        val newItem = FakeData.toDoItem.copy(
            description = "New Description"
        )
        runBlocking {
            toDoItemRepository.insert(FakeData.toDoItem)
            useCase.execute(newItem)
        }

        runBlocking {
            val toDoItem = toDoItemRepository
                .getById(newItem.id)
                .first()
            assertNotNull(toDoItem)
            assert(createAlarmUseCaseFake.wasExecuted)
        }
    }

    @Test
    fun not_updates_invalid_item() {
        val toDoItemRepository = ToDoItemRepositoryFake()
        val createAlarmUseCaseFake = CreateAlarmUseCaseFake()
        val useCase = UpdateItemUseCaseImpl(
            toDoItemRepository,
            ValidateItemUseCaseImpl(),
            createAlarmUseCaseFake
        )

        val newItem = FakeData.toDoItemInvalid.copy(
            description = "New Description"
        )
        runBlocking {
            toDoItemRepository.insert(FakeData.toDoItem)
            useCase.execute(newItem)
        }

        runBlocking {
            val toDoItem = toDoItemRepository
                .getById(newItem.id)
                .first()
            assertNotNull(toDoItem)
            assertNotEquals(toDoItem?.description, newItem.description)
            assert(!createAlarmUseCaseFake.wasExecuted)
        }
    }
}