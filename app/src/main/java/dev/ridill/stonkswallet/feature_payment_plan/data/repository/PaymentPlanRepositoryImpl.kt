package dev.ridill.stonkswallet.feature_payment_plan.data.repository

import dev.ridill.stonkswallet.core.util.DispatcherProvider
import dev.ridill.stonkswallet.feature_payment_plan.data.local.PaymentPlanDao
import dev.ridill.stonkswallet.feature_payment_plan.data.local.entity.PaymentPlanEntity
import dev.ridill.stonkswallet.feature_payment_plan.data.local.relation.PaymentPlanRelation
import dev.ridill.stonkswallet.feature_payment_plan.domain.repository.PaymentPlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PaymentPlanRepositoryImpl(
    private val dao: PaymentPlanDao,
    private val dispatcherProvider: DispatcherProvider
) : PaymentPlanRepository {

    override fun getAllPaymentPlans(): Flow<List<PaymentPlanEntity>> =
        dao.getPaymentPlansList()

    override fun getPaymentsForMonth(monthAndYear: String): Flow<List<PaymentPlanRelation>> =
        dao.getPaymentsForMonth(monthAndYear)

    override suspend fun getUpcomingPlans(
        date: Long,
        maxDaysAhead: Int
    ): List<PaymentPlanEntity> =
        withContext(dispatcherProvider.io) {
            dao.getUpcomingPaymentPlans(date, maxDaysAhead)
        }

    override suspend fun getPlanById(id: Long): PaymentPlanEntity? =
        withContext(dispatcherProvider.io) {
            dao.getPaymentPlanById(id)
        }

    override suspend fun incrementNextDueDate(billId: Long, byFactor: Int) =
        withContext(dispatcherProvider.io) {
            dao.incrementNextDueDateForPlan(billId, byFactor)
        }

    override suspend fun decrementNextDueDate(billId: Long, byFactor: Int) =
        withContext(dispatcherProvider.io) {
            dao.decrementNextDueDateForPlan(billId, byFactor)
        }

    override suspend fun cachePaymentPlan(bill: PaymentPlanEntity) =
        withContext(dispatcherProvider.io) {
            dao.insert(bill)
        }

    override suspend fun deletePaymentPlan(id: Long) = withContext(dispatcherProvider.io) {
        dao.deleteById(id)
    }

    override suspend fun getTotalPaymentPlanCount(): Int = withContext(dispatcherProvider.io) {
        dao.getPaymentPlanCount()
    }
}