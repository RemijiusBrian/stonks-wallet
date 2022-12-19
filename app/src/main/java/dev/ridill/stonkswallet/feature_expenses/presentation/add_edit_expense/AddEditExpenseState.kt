package dev.ridill.stonkswallet.feature_expenses.presentation.add_edit_expense

import dev.ridill.stonkswallet.feature_expenses.domain.model.Tag

data class AddEditExpenseState(
    val tagsList: List<Tag> = emptyList(),
    val selectedTag: Tag? = null,
    val savable: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    val showTagInput: Boolean = false,
    val showDiscardChangesMessage: Boolean = false
) {
    companion object {
        val INITIAL = AddEditExpenseState()
    }
}