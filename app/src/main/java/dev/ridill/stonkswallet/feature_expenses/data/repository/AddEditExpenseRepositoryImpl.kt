package dev.ridill.stonkswallet.feature_expenses.data.repository

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import dev.ridill.stonkswallet.feature_expenses.data.local.entity.ExpenseEntity
import dev.ridill.stonkswallet.feature_expenses.data.mapper.toEntity
import dev.ridill.stonkswallet.feature_expenses.data.mapper.toExpense
import dev.ridill.stonkswallet.feature_expenses.data.mapper.toTag
import dev.ridill.stonkswallet.feature_expenses.domain.model.Expense
import dev.ridill.stonkswallet.feature_expenses.domain.model.Tag
import dev.ridill.stonkswallet.feature_expenses.domain.repository.AddEditExpenseRepository
import dev.ridill.stonkswallet.feature_expenses.domain.repository.ExpenseRepository
import dev.ridill.stonkswallet.feature_expenses.domain.repository.TagsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AddEditExpenseRepositoryImpl(
    private val expenseRepo: ExpenseRepository,
    private val tagsRepo: TagsRepository
) : AddEditExpenseRepository {

    override suspend fun saveExpense(
        id: Long,
        note: String,
        amount: Double,
        dateMillis: Long,
        tag: String?
    ): Long {
        val entity = ExpenseEntity(
            id = id,
            note = note,
            amount = amount,
            dateMillis = dateMillis,
            tag = tag
        )
        return expenseRepo.cacheExpense(entity)
    }

    override suspend fun deleteExpense(expenseId: Long, billId: Long?) =
        expenseRepo.deleteExpenseById(expenseId, billId)

    override suspend fun getExpense(id: Long): Expense? =
        expenseRepo.getExpenseById(id)?.toExpense()

    override fun getTags(): Flow<List<Tag>> = tagsRepo.getAllTags().map { entities ->
        entities.map { it.toTag() }
    }

    override suspend fun createTag(name: String, color: Color): Tag {
        val tag = Tag(name, color.toArgb())
        tagsRepo.cacheTag(tag.toEntity())
        return tag
    }
}