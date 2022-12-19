package dev.ridill.stonkswallet.feature_payment_plan.domain.model

data class PaymentPlanListItem(
    val id: Long,
    val name: String,
    val category: PaymentCategory,
    val amount: String,
    val dueDate: String
)