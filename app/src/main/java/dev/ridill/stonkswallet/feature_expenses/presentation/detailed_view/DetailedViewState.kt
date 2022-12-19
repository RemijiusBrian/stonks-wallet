package dev.ridill.stonkswallet.feature_expenses.presentation.detailed_view

import androidx.compose.ui.state.ToggleableState
import dev.ridill.stonkswallet.feature_expenses.domain.model.Expense
import dev.ridill.stonkswallet.feature_expenses.domain.model.TagOverview

data class DetailedViewState(
    val tagOverviews: List<TagOverview> = emptyList(),
    val selectedTag: String? = null,
    val totalExpenditure: String = "",
    val showTagInput: Boolean = false,
    val yearsList: List<String> = emptyList(),
    val selectedYear: String = "",
    val selectedMonth: Int = 1,
    val expenses: List<Expense> = emptyList(),
    val showTagDeletionConfirmation: Boolean = false,
    val multiSelectionModeActive: Boolean = false,
    val selectedExpenseIds: List<Long> = emptyList(),
    val expenseSelectionState: ToggleableState = ToggleableState.Off,
    val showExpenseDeleteConfirmation: Boolean = false
) {
    companion object {
        val INITIAL = DetailedViewState()
    }
}