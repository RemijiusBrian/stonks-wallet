package dev.ridill.stonkswallet.feature_payment_plan.data.repository

import dev.ridill.stonkswallet.core.util.*
import dev.ridill.stonkswallet.feature_expenses.data.local.entity.ExpenseEntity
import dev.ridill.stonkswallet.feature_expenses.domain.repository.ExpenseRepository
import dev.ridill.stonkswallet.feature_payment_plan.data.mapper.toPaymentPlanListItem
import dev.ridill.stonkswallet.feature_payment_plan.data.mapper.toPaymentPlanExpense
import dev.ridill.stonkswallet.feature_payment_plan.domain.model.*
import dev.ridill.stonkswallet.feature_payment_plan.domain.repository.PaymentPlanListRepository
import dev.ridill.stonkswallet.feature_payment_plan.domain.repository.PaymentPlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PaymentPlanListRepositoryImpl(
    private val paymentPlanRepository: PaymentPlanRepository,
    private val expenseRepository: ExpenseRepository
) : PaymentPlanListRepository {

    override fun getPlansByCategory(): Flow<Map<PaymentCategory, List<PaymentPlanListItem>>> =
        paymentPlanRepository.getAllPaymentPlans().map { entities ->
            entities.map { it.toPaymentPlanListItem() }
        }.map { bills ->
            bills.groupBy { it.category }
        }

    override fun getPaymentsByStatus(monthAndYearMillis: Long): Flow<Map<PaymentStatus, List<PaymentPlanExpense>>> {
        val date = monthAndYearMillis.toLocalDate()
        return paymentPlanRepository.getPaymentsForMonth(date.format(DatePatterns.MM_HYPHEN_YYYY))
            .map { relations ->
                relations.map { it.toPaymentPlanExpense(isDueAfter = { dueDateMillis -> dueDateMillis >= date.timeMillis }) }
            }.map { payments ->
                payments.groupBy { it.status }
            }
    }

    override suspend fun markAsPaid(payment: PaymentPlanExpense) {
        val expense = ExpenseEntity(
            note = PaymentPlan.buildExpenseNote(payment.name, payment.category),
            amount = payment.amount,
            paymentPlanId = payment.billId,
            dateMillis = DateUtil.currentEpochMillis()
        )
        expenseRepository.cacheExpense(expense)
        paymentPlanRepository.incrementNextDueDate(payment.billId)
    }
}