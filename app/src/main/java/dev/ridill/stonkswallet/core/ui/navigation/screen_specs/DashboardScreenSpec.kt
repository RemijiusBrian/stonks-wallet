package dev.ridill.stonkswallet.core.ui.navigation.screen_specs

import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import dev.ridill.stonkswallet.R
import dev.ridill.stonkswallet.core.ui.components.rememberSnackbarController
import dev.ridill.stonkswallet.core.util.exhaustive
import dev.ridill.stonkswallet.feature_dashboard.ui.dashboard.DashboardScreen
import dev.ridill.stonkswallet.feature_dashboard.ui.dashboard.DashboardState
import dev.ridill.stonkswallet.feature_dashboard.ui.dashboard.DashboardViewModel
import dev.ridill.stonkswallet.feature_expenses.presentation.add_edit_expense.ADD_EDIT_EXPENSE_RESULT

object DashboardScreenSpec : ScreenSpec {

    override val label: Int = R.string.destination_dashboard

    override val navHostRoute: String = "dashboard"

    @Composable
    override fun Content(navController: NavController, navBackStackEntry: NavBackStackEntry) {
        val viewModel: DashboardViewModel = hiltViewModel(navBackStackEntry)
        val state by viewModel.state.observeAsState(DashboardState.INITIAL)
        val context = LocalContext.current
        val snackbarController = rememberSnackbarController()

        // Add/Edit Expense Result
        val addEditExpenseResult = navBackStackEntry
            .savedStateHandle.getLiveData<String>(ADD_EDIT_EXPENSE_RESULT).observeAsState()
        LaunchedEffect(addEditExpenseResult) {
            navBackStackEntry.savedStateHandle
                .remove<String>(ADD_EDIT_EXPENSE_RESULT)
            addEditExpenseResult.value?.let(viewModel::onAddEditResult)
        }

        // Collect Events
        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                    is DashboardViewModel.DashboardEvent.NavigateToAddEditExpenseScreen -> {
                        navController.navigate(
                            AddEditExpenseScreenSpec.buildRoute(event.id)
                        )
                    }
                    is DashboardViewModel.DashboardEvent.ShowUiMessage -> {
                        snackbarController.showSnackbar(event.message.asString(context))
                    }
                    is DashboardViewModel.DashboardEvent.ShowExpenseDeleteUndo -> {
                        snackbarController.showSnackbar(
                            message = context.getString(R.string.expense_deleted),
                            actionLabel = context.getString(R.string.action_undo),
                            onActionPerformed = { result ->
                                if (result == SnackbarResult.ActionPerformed) {
                                    viewModel.onExpenseDeleteUndo(event.expense)
                                }
                            }
                        )
                    }
                }.exhaustive
            }
        }

        DashboardScreen(
            state = state,
            snackbarController = snackbarController,
            actions = viewModel
        ) {
            navController.navigate(it.navHostRoute)
        }
    }
}