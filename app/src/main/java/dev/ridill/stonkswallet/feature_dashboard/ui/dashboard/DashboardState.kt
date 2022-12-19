package dev.ridill.stonkswallet.feature_dashboard.ui.dashboard

import dev.ridill.stonkswallet.feature_expenses.domain.model.Expense

data class DashboardState(
    val expenditureLimit: Long = 0L,
    val expenses: List<Expense> = emptyList(),
    val expenditure: Double = 0.0,
    val balance: Double = 0.0,
    val balancePercent: Float = 0f,
    val showLowBalanceWarning: Boolean = false,
    val isLimitSet: Boolean = false,
) {
    companion object {
        val INITIAL: DashboardState = DashboardState()
    }
}