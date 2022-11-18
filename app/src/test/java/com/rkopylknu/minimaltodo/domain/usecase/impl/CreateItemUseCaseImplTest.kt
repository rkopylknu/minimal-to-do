package com.rkopylknu.minimaltodo.domain.usecase.impl

import com.rkopylknu.minimaltodo.util.CreateAlarmUseCaseFake
import com.rkopylknu.minimaltodo.util.FakeData
import com.rkopylknu.minimaltodo.util.ToDoItemRepositoryFake
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test

class CreateItemUseCaseImplTest {

    @Test
    fun inserts_valid_item() {
        val toDoItemRepository = ToDoItemRepositoryFake()
        val createAlarmUseCase = CreateAlarmUseCaseFake()
        val useCase = CreateItemUseCaseImpl(
            toDoItemRepository,
            createAlarmUseCase,
            ValidateItemUseCaseImpl()
        )

        runBlocking {
            useCase.execute(FakeData.toDoItem)
        }

        runBlocking {
            val toDoItem = toDoItemRepository
                .getById(FakeData.toDoItem.id)
                .first()
            assert(toDoItem != null)
            assert(createAlarmUseCase.wasExecuted)
            assert(toDoItem?.color != 0)
        }
    }

    @Test
    fun not_inserts_invalid_item() {
        val toDoItemRepository = ToDoItemRepositoryFake()
        val createAlarmUseCase = CreateAlarmUseCaseFake()
        val useCase = CreateItemUseCaseImpl(
            toDoItemRepository,
            createAlarmUseCase,
            ValidateItemUseCaseImpl()
        )

        runBlocking {
            useCase.execute(FakeData.toDoItemInvalid)
        }

        runBlocking {
            val toDoItem = toDoItemRepository
                .getById(FakeData.toDoItemInvalid.id)
                .first()
            assert(toDoItem == null)
            assert(!createAlarmUseCase.wasExecuted)
        }
    }
}