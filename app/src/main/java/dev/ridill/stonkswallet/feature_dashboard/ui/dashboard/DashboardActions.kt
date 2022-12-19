package dev.ridill.stonkswallet.feature_dashboard.ui.dashboard

import dev.ridill.stonkswallet.feature_expenses.domain.model.Expense

interface DashboardActions {
    fun onAddFabClick()
    fun onExpenseClick(id: Long)
    fun onExpenseSwipe(expense: Expense)
    fun onExpenseDeleteUndo(expense: Expense)
}