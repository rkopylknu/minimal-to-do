package com.rkopylknu.minimaltodo.domain.usecase.impl

import com.rkopylknu.minimaltodo.util.FakeData
import com.rkopylknu.minimaltodo.util.ToDoItemRepositoryFake
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DeleteItemUseCaseImplTest {

    @Test
    fun deletes_existing_item_empty_repository() {
        val toDoItemRepository = ToDoItemRepositoryFake()
        val useCase = DeleteItemUseCaseImpl(toDoItemRepository)

        runBlocking {
            toDoItemRepository.insert(FakeData.toDoItem)
            useCase.execute(FakeData.toDoItem.id)
        }

        runBlocking {
            assertNull(
                toDoItemRepository
                    .getById(FakeData.toDoItem.id)
                    .first()
            )
        }
    }

    @Test
    fun delete_existing_item_full_repository() {
        val toDoItemRepository = ToDoItemRepositoryFake()
        val useCase = DeleteItemUseCaseImpl(toDoItemRepository)

        runBlocking {
            toDoItemRepository.insert(FakeData.toDoItemList[0])
            toDoItemRepository.insert(FakeData.toDoItemList[1])
            toDoItemRepository.insert(FakeData.toDoItem)
            useCase.execute(FakeData.toDoItem.id)
        }

        runBlocking {
            assertNull(
                toDoItemRepository
                    .getById(FakeData.toDoItem.id)
                    .first()
            )
            assertEquals(
                toDoItemRepository.getAll().first().size,
                2
            )
        }
    }

    @Test
    fun not_deletes_unexisting_item() {
        val toDoItemRepository = ToDoItemRepositoryFake()
        val useCase = DeleteItemUseCaseImpl(toDoItemRepository)

        runBlocking {
            useCase.execute(FakeData.toDoItem.id)
        }

        runBlocking {
            assertNull(
                toDoItemRepository
                    .getById(FakeData.toDoItem.id)
                    .first()
            )
        }
    }
}