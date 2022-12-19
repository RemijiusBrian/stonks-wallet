package dev.ridill.stonkswallet.feature_expenses.domain.repository

import androidx.compose.ui.graphics.Color
import dev.ridill.stonkswallet.feature_expenses.domain.model.Expense
import dev.ridill.stonkswallet.feature_expenses.domain.model.Tag
import kotlinx.coroutines.flow.Flow

interface AddEditExpenseRepository {

    suspend fun saveExpense(
        id: Long,
        note: String,
        amount: Double,
        dateMillis: Long,
        tag: String? = null
    ): Long

    suspend fun deleteExpense(expenseId: Long, billId: Long?)

    suspend fun getExpense(id: Long): Expense?

    fun getTags(): Flow<List<Tag>>

    suspend fun createTag(name: String, color: Color): Tag
}