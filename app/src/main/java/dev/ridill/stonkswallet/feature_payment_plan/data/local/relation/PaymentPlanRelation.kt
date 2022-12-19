package dev.ridill.stonkswallet.feature_payment_plan.data.local.relation

data class PaymentPlanRelation(
    val billId: Long,
    val billName: String,
    val nextDueDateMillis: Long,
    val amount: Double,
    val category: String,
    val expenseId: Long?
)