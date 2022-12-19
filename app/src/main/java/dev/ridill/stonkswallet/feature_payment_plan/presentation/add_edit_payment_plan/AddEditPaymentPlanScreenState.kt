package dev.ridill.stonkswallet.feature_payment_plan.presentation.add_edit_payment_plan

import dev.ridill.stonkswallet.feature_payment_plan.domain.model.PaymentCategory

data class AddEditPaymentPlanScreenState(
    val showCategorySelection: Boolean = false,
    val showDeletionConfirmation: Boolean = false,
    val dueDate: String = "",
    val category: PaymentCategory = PaymentCategory.MISC,
    val showDatePicker: Boolean = false
) {
    companion object {
        val INITIAL = AddEditPaymentPlanScreenState()
    }
}