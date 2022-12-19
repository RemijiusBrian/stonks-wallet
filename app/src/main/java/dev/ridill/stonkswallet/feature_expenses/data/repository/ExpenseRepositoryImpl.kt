package dev.ridill.stonkswallet.feature_expenses.data.repository

import dev.ridill.stonkswallet.core.util.DispatcherProvider
import dev.ridill.stonkswallet.feature_expenses.data.local.ExpenseDao
import dev.ridill.stonkswallet.feature_expenses.data.local.entity.ExpenseEntity
import dev.ridill.stonkswallet.feature_expenses.data.local.relation.ExpenseWithTagRelation
import dev.ridill.stonkswallet.feature_expenses.domain.repository.ExpenseRepository
import dev.ridill.stonkswallet.feature_payment_plan.data.local.PaymentPlanDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ExpenseRepositoryImpl(
    private val dao: ExpenseDao,
    private val dispatcherProvider: DispatcherProvider,
    private val paymentPlanDao: PaymentPlanDao
) : ExpenseRepository {

    override fun getYearsOfExpenses(): Flow<List<Int>> = dao.getDistinctYears()

    override fun getExpensesForDate(monthAndYear: String): Flow<List<ExpenseWithTagRelation>> =
        dao.getExpensesForDate(monthAndYear)

    override fun getExpenditureForDate(monthAndYear: String): Flow<Double> =
        dao.getExpenditureForDate(monthAndYear)

    override suspend fun getExpenseById(id: Long): ExpenseWithTagRelation? =
        withContext(dispatcherProvider.io) {
            dao.getExpenseById(id)
        }

    override suspend fun cacheExpense(entity: ExpenseEntity): Long =
        withContext(dispatcherProvider.io) {
            dao.insert(entity)
        }

    override suspend fun deleteExpenseById(id: Long, billId: Long?) =
        withContext(dispatcherProvider.io) {
            billId?.let { paymentPlanDao.decrementNextDueDateForPlan(it, 1) }
            dao.deleteById(id)
        }

    override suspend fun deleteMultipleExpenses(ids: List<Long>) =
        withContext(dispatcherProvider.io) {
            ids.forEach { id ->
                getExpenseById(id)?.let {
                    deleteExpenseById(
                        it.expenseEntity.id,
                        it.expenseEntity.paymentPlanId
                    )
                }
            }
        }
}

