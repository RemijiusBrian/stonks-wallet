package dev.ridill.stonkswallet.feature_expenses.domain.repository

import dev.ridill.stonkswallet.feature_expenses.data.local.entity.ExpenseEntity
import dev.ridill.stonkswallet.feature_expenses.data.local.relation.ExpenseWithTagRelation
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {

    fun getYearsOfExpenses(): Flow<List<Int>>

    fun getExpensesForDate(monthAndYear: String): Flow<List<ExpenseWithTagRelation>>

    fun getExpenditureForDate(monthAndYear: String): Flow<Double>

    suspend fun getExpenseById(id: Long): ExpenseWithTagRelation?

    suspend fun cacheExpense(entity: ExpenseEntity): Long

    suspend fun deleteExpenseById(id: Long, billId: Long? = null)

    suspend fun deleteMultipleExpenses(ids: List<Long>)
}