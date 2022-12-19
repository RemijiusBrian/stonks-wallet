package dev.ridill.stonkswallet.feature_dashboard.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.zhuinden.flowcombinetuplekt.combineTuple
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ridill.stonkswallet.R
import dev.ridill.stonkswallet.core.domain.model.UiText
import dev.ridill.stonkswallet.feature_dashboard.domain.repository.DashboardRepository
import dev.ridill.stonkswallet.feature_expenses.domain.model.Expense
import dev.ridill.stonkswallet.feature_expenses.presentation.add_edit_expense.RESULT_EXPENSE_ADDED
import dev.ridill.stonkswallet.feature_expenses.presentation.add_edit_expense.RESULT_EXPENSE_DELETED
import dev.ridill.stonkswallet.feature_expenses.presentation.add_edit_expense.RESULT_EXPENSE_UPDATED
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repo: DashboardRepository
) : ViewModel(), DashboardActions {

    private val expenditureLimit = repo.getExpenditureLimit()
    private val expenditureLimitSet = repo.isExpenditureLimitSet()
    private val showBalanceWarning = repo.showBalanceWarning()
    private val balanceWarningPercent = repo.balanceWarningPercent()

    private val expenseList = repo.getExpenseList()
    private val expenditure = repo.getExpenditure()
    private val balance = combineTuple(
        expenditure,
        expenditureLimit
    ).map { (expenditure, expenditureLimit) ->
        expenditureLimit - expenditure
    }
    private val balancePercent = combineTuple(
        balance,
        expenditureLimit
    ).map { (balance, limit) ->
        (balance / limit).toFloat().coerceIn(0f..1f)
    }

    val state = combineTuple(
        expenditureLimit,
        expenditureLimitSet,
        showBalanceWarning,
        balanceWarningPercent,
        expenseList,
        expenditure,
        balance,
        balancePercent
    ).map { (
                expenditureLimit,
                expenditureLimitSet,
                showBalanceWarning,
                balanceWarningPercent,
                expenseList,
                expenditure,
                balance,
                balancePercent
            ) ->
        DashboardState(
            expenditureLimit = expenditureLimit,
            expenses = expenseList,
            expenditure = expenditure,
            balance = balance,
            balancePercent = balancePercent,
            isLimitSet = expenditureLimitSet,
            showLowBalanceWarning = showBalanceWarning && balancePercent <= balanceWarningPercent
        )
    }.asLiveData()

    private val eventsChannel = Channel<DashboardEvent>()
    val events get() = eventsChannel.receiveAsFlow()

    override fun onAddFabClick() {
        viewModelScope.launch {
            eventsChannel.send(
                DashboardEvent.NavigateToAddEditExpenseScreen(-1L)
            )
        }
    }

    override fun onExpenseClick(id: Long) {
        viewModelScope.launch {
            eventsChannel.send(
                DashboardEvent.NavigateToAddEditExpenseScreen(id)
            )
        }
    }

    fun onAddEditResult(result: String) {
        val messageRes = when (result) {
            RESULT_EXPENSE_ADDED -> R.string.message_expense_added
            RESULT_EXPENSE_UPDATED -> R.string.message_expense_updated
            RESULT_EXPENSE_DELETED -> R.string.message_expense_deleted
            else -> return
        }
        viewModelScope.launch {
            eventsChannel.send(DashboardEvent.ShowUiMessage(UiText.StringResource(messageRes)))
        }
    }

    override fun onExpenseSwipe(expense: Expense) {
        viewModelScope.launch {
            repo.deleteExpense(expense.id, expense.paymentPlanId)
            eventsChannel.send(DashboardEvent.ShowExpenseDeleteUndo(expense))
        }
    }

    override fun onExpenseDeleteUndo(expense: Expense) {
        viewModelScope.launch {
            repo.undoExpenseDelete(expense)
        }
    }

    sealed class DashboardEvent {
        data class ShowUiMessage(val message: UiText) : DashboardEvent()
        data class ShowExpenseDeleteUndo(val expense: Expense) : DashboardEvent()
        data class NavigateToAddEditExpenseScreen(val id: Long) : DashboardEvent()
    }
}