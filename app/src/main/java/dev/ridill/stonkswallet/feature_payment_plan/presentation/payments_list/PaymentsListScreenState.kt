package dev.ridill.stonkswallet.feature_payment_plan.presentation.payments_list

import dev.ridill.stonkswallet.feature_payment_plan.domain.model.PaymentCategory
import dev.ridill.stonkswallet.feature_payment_plan.domain.model.PaymentPlanListItem
import dev.ridill.stonkswallet.feature_payment_plan.domain.model.PaymentPlanExpense
import dev.ridill.stonkswallet.feature_payment_plan.domain.model.PaymentStatus

data class PaymentsListScreenState(
    val billsList: Map<PaymentCategory, List<PaymentPlanListItem>> = emptyMap(),
    val paymentsPlanExpense: Map<PaymentStatus, List<PaymentPlanExpense>> = emptyMap()
) {
    companion object {
        val INITIAL = PaymentsListScreenState()
    }
}