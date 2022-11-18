package com.rkopylknu.minimaltodo.domain.usecase.impl

import com.rkopylknu.minimaltodo.util.CreateAlarmUseCaseFake
import com.rkopylknu.minimaltodo.util.FakeData
import com.rkopylknu.minimaltodo.util.ToDoItemRepositoryFake
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class RestoreItemUseCaseImplTest {

    @Test
    fun restores_item() {
        val toDoItemRepository = ToDoItemRepositoryFake()
        val createAlarmUseCase = CreateAlarmUseCaseFake()
        val useCase = RestoreItemUseCaseImpl(
            toDoItemRepository,
            createAlarmUseCase
        )

        runBlocking {
            useCase.execute(FakeData.toDoItem)
        }

        runBlocking {
            val toDoItem = toDoItemRepository
                .getById(FakeData.toDoItem.id)
                .first()
            assertNotNull(toDoItem)
            assert(createAlarmUseCase.wasExecuted)
        }
    }
}