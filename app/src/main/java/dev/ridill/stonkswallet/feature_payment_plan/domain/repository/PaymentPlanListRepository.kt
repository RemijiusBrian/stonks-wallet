package dev.ridill.stonkswallet.feature_payment_plan.domain.repository

import dev.ridill.stonkswallet.feature_payment_plan.domain.model.PaymentCategory
import dev.ridill.stonkswallet.feature_payment_plan.domain.model.PaymentPlanListItem
import dev.ridill.stonkswallet.feature_payment_plan.domain.model.PaymentPlanExpense
import dev.ridill.stonkswallet.feature_payment_plan.domain.model.PaymentStatus
import kotlinx.coroutines.flow.Flow

interface PaymentPlanListRepository {

    fun getPlansByCategory(): Flow<Map<PaymentCategory, List<PaymentPlanListItem>>>

    fun getPaymentsByStatus(monthAndYearMillis: Long): Flow<Map<PaymentStatus, List<PaymentPlanExpense>>>

    suspend fun markAsPaid(payment: PaymentPlanExpense)
}