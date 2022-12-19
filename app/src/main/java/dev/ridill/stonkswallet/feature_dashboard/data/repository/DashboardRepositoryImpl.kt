package dev.ridill.stonkswallet.feature_dashboard.data.repository

import dev.ridill.stonkswallet.core.data.preferences.PreferencesManager
import dev.ridill.stonkswallet.core.util.DatePatterns
import dev.ridill.stonkswallet.core.util.DateUtil
import dev.ridill.stonkswallet.core.util.format
import dev.ridill.stonkswallet.feature_dashboard.domain.repository.DashboardRepository
import dev.ridill.stonkswallet.feature_expenses.data.mapper.toEntity
import dev.ridill.stonkswallet.feature_expenses.data.mapper.toExpense
import dev.ridill.stonkswallet.feature_expenses.domain.model.Expense
import dev.ridill.stonkswallet.feature_expenses.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class DashboardRepositoryImpl(
    preferencesManager: PreferencesManager,
    private val expenseRepo: ExpenseRepository
) : DashboardRepository {

    private val preferences = preferencesManager.preferences

    private val currentDate = DateUtil.currentDate()
        .format(DatePatterns.MM_HYPHEN_YYYY)

    override fun getExpenditureLimit(): Flow<Long> =
        preferences.map { it.expenditureLimit }.distinctUntilChanged()

    override fun isExpenditureLimitSet(): Flow<Boolean> =
        preferences.map { it.isExpenditureLimitSet }.distinctUntilChanged()

    override fun showBalanceWarning(): Flow<Boolean> =
        preferences.map { it.balanceWarningEnabled }.distinctUntilChanged()

    override fun balanceWarningPercent(): Flow<Float> =
        preferences.map { it.balanceWarningPercent }.distinctUntilChanged()

    override fun getExpenseList(): Flow<List<Expense>> =
        expenseRepo.getExpensesForDate(currentDate).map { entities ->
            entities.map { it.toExpense() }
        }

    override fun getExpenditure(): Flow<Double> =
        expenseRepo.getExpenditureForDate(currentDate)

    override suspend fun deleteExpense(id: Long, billId: Long?): Unit =
        expenseRepo.deleteExpenseById(id, billId)

    override suspend fun undoExpenseDelete(expense: Expense): Long =
        expenseRepo.cacheExpense(expense.toEntity())
}