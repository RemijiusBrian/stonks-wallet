package dev.ridill.stonkswallet.feature_payment_plan.domain.repository

import dev.ridill.stonkswallet.feature_payment_plan.data.local.entity.PaymentPlanEntity
import dev.ridill.stonkswallet.feature_payment_plan.data.local.relation.PaymentPlanRelation
import kotlinx.coroutines.flow.Flow

interface PaymentPlanRepository {

    fun getAllPaymentPlans(): Flow<List<PaymentPlanEntity>>

    fun getPaymentsForMonth(monthAndYear: String): Flow<List<PaymentPlanRelation>>

    suspend fun getUpcomingPlans(date: Long, maxDaysAhead: Int = 3): List<PaymentPlanEntity>

    suspend fun getPlanById(id: Long): PaymentPlanEntity?

    suspend fun incrementNextDueDate(billId: Long, byFactor: Int = 1)

    suspend fun decrementNextDueDate(billId: Long, byFactor: Int = 1)

    suspend fun cachePaymentPlan(bill: PaymentPlanEntity)

    suspend fun deletePaymentPlan(id: Long)

    suspend fun getTotalPaymentPlanCount(): Int
}