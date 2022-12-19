package dev.ridill.stonkswallet.feature_dashboard.domain.repository

import dev.ridill.stonkswallet.feature_expenses.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {

    fun getExpenditureLimit(): Flow<Long>

    fun isExpenditureLimitSet(): Flow<Boolean>

    fun showBalanceWarning(): Flow<Boolean>

    fun balanceWarningPercent(): Flow<Float>

    fun getExpenseList(): Flow<List<Expense>>

    fun getExpenditure(): Flow<Double>

    suspend fun deleteExpense(id: Long, billId: Long?)

    suspend fun undoExpenseDelete(expense: Expense): Long
}