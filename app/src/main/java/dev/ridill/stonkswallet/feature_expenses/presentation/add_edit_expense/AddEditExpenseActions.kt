package dev.ridill.stonkswallet.feature_expenses.presentation.add_edit_expense

import androidx.compose.ui.graphics.Color
import dev.ridill.stonkswallet.feature_expenses.domain.model.Tag

interface AddEditExpenseActions {
    fun onAmountChange(value: String)
    fun onNoteChange(value: String)
    fun onTagSelect(tag: Tag)
    fun onNewTagClick()
    fun onNewTagDismiss()
    fun onNewTagConfirm(name: String, color: Color)
    fun onDeleteClick()
    fun onDeleteDismiss()
    fun onDeleteConfirm()
    fun onSave()
    fun onTryNavigateUp()
    fun onDiscardChangedDismissed()
    fun onDiscardChangedConfirmed()
}