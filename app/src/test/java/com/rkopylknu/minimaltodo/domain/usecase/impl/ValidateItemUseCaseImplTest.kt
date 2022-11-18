package com.rkopylknu.minimaltodo.domain.usecase.impl

import com.rkopylknu.minimaltodo.util.FakeData
import org.junit.Assert.*
import org.junit.Test

class ValidateItemUseCaseImplTest {

    @Test
    fun validates_valid_item() {
        val useCase = ValidateItemUseCaseImpl()
        val isValid = useCase.execute(FakeData.toDoItem)
        assert(isValid)
    }

    @Test
    fun validates_invalid_item() {
        val useCase = ValidateItemUseCaseImpl()
        val isValid = useCase.execute(FakeData.toDoItemInvalid)
        assert(!isValid)
    }
}