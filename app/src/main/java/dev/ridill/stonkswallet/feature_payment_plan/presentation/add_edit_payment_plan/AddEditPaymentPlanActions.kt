package dev.ridill.stonkswallet.feature_payment_plan.presentation.add_edit_payment_plan

import dev.ridill.stonkswallet.feature_payment_plan.domain.model.PaymentCategory

interface AddEditPaymentPlanActions {
    fun onNameChange(value: String)
    fun onAmountChange(value: String)
    fun onCategoryClick()
    fun onCategorySelectionDismiss()
    fun onCategorySelect(category: PaymentCategory)
    fun onDueDateChange(dateMillis: Long)
    fun onDatePickerClick()
    fun onDatePickerDismiss()
    fun onRepeatMonthsPeriodChange(months: String)
    fun onSave()

    fun onDeleteClick()
    fun onDeleteDismiss()
    fun onDeleteConfirm()
}