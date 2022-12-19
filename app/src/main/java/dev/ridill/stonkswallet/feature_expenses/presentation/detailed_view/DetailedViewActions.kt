package dev.ridill.stonkswallet.feature_expenses.presentation.detailed_view

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import java.time.Month

interface DetailedViewActions {
    fun onTagSelect(tag: String)
    fun onTagDelete(tag: String)
    fun onTagDeleteDismiss()
    fun onTagDeleteConfirm()
    fun onNewTagClick()
    fun onNewTagDismiss()
    fun onNewTagConfirm(name: String, color: Color)
    fun onYearSelect(year: String)
    fun onMonthSelect(month: Month)
    fun onExpenseLongClick(id: Long)
    fun onExpenseSelectionToggle(id: Long)
    fun onDismissMultiSelectionMode()
    fun onSelectionStateChange(currentState: ToggleableState)
    fun onDeleteExpensesClick()
    fun onDeleteExpensesDismissed()
    fun onDeleteExpensesConfirmed()
    fun onUntagExpensesClick()
}