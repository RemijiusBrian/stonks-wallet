package dev.ridill.stonkswallet.feature_payment_plan.presentation.payments_list

import dev.ridill.stonkswallet.feature_payment_plan.domain.model.PaymentPlanExpense

interface PaymentPlansListActions {
    fun onMarkAsPaidClick(payment: PaymentPlanExpense)
    fun onPlanClick(id: Long)
}