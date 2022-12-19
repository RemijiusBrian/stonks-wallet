package dev.ridill.stonkswallet.core.ui.navigation.screen_specs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import dev.ridill.stonkswallet.R
import dev.ridill.stonkswallet.core.ui.components.rememberSnackbarController
import dev.ridill.stonkswallet.core.util.exhaustive
import dev.ridill.stonkswallet.feature_expenses.presentation.add_edit_expense.*

object AddEditExpenseScreenSpec : ScreenSpec {

    override val label: Int = R.string.destination_add_edit_expense

    override val navHostRoute: String = "add_edit_expense?$ARG_EXPENSE_ID={$ARG_EXPENSE_ID}"

    override val arguments: List<NamedNavArgument>
        get() = listOf(
            navArgument(ARG_EXPENSE_ID) {
                type = NavType.LongType
                nullable = false
                defaultValue = NO_EXPENSE_ID
            }
        )

    override val deepLinks: List<NavDeepLink>
        get() = listOf(
            navDeepLink {
                uriPattern =
                    "https://www.xpensetracker.ridill.dev/add_edit_expense/{$ARG_EXPENSE_ID}"
            }
        )

    fun isEditMode(expenseId: Long?): Boolean =
        expenseId != null && expenseId != NO_EXPENSE_ID

    fun buildRoute(expenseId: Long = NO_EXPENSE_ID): String =
        "add_edit_expense?$ARG_EXPENSE_ID=$expenseId"

    fun getExpenseIdFromSavedStateHandle(savedStateHandle: SavedStateHandle): Long? =
        savedStateHandle.get<Long>(ARG_EXPENSE_ID)

    @Composable
    override fun Content(navController: NavController, navBackStackEntry: NavBackStackEntry) {
        val viewModel: AddEditExpenseViewModel = hiltViewModel(navBackStackEntry)
        val amountInput by viewModel.amountInput.observeAsState("")
        val noteInput by viewModel.noteInput.observeAsState("")
        val state by viewModel.state.observeAsState(AddEditExpenseState.INITIAL)

        val snackbarController = rememberSnackbarController()
        val context = LocalContext.current
        val keyboardController = LocalSoftwareKeyboardController.current

        LaunchedEffect(snackbarController, context) {
            @Suppress("IMPLICIT_CAST_TO_ANY")
            viewModel.events.collect { event ->
                when (event) {
                    AddEditExpenseViewModel.AddEditExpenseEvent.ExpenseCreated -> {
                        keyboardController?.hide()
                        navController.previousBackStackEntry?.savedStateHandle
                            ?.set(ADD_EDIT_EXPENSE_RESULT, RESULT_EXPENSE_ADDED)
                        navController.popBackStack()
                    }
                    AddEditExpenseViewModel.AddEditExpenseEvent.ExpenseDeleted -> {
                        keyboardController?.hide()
                        navController.previousBackStackEntry?.savedStateHandle
                            ?.set(ADD_EDIT_EXPENSE_RESULT, RESULT_EXPENSE_DELETED)
                        navController.popBackStack()
                    }
                    AddEditExpenseViewModel.AddEditExpenseEvent.ExpenseUpdated -> {
                        keyboardController?.hide()
                        navController.previousBackStackEntry?.savedStateHandle
                            ?.set(ADD_EDIT_EXPENSE_RESULT, RESULT_EXPENSE_UPDATED)
                        navController.popBackStack()
                    }
                    is AddEditExpenseViewModel.AddEditExpenseEvent.ShowUiMessage -> {
                        snackbarController.showSnackbar(
                            event.message.asString(context),
                            event.error
                        )
                    }
                    AddEditExpenseViewModel.AddEditExpenseEvent.NavigateUp -> {
                        navController.navigateUp()
                    }
                }.exhaustive
            }
        }

        AddEditExpenseScreenContent(
            snackbarController = snackbarController,
            isEditMode = viewModel.editMode,
            amountInput = amountInput.toString(),
            noteInput = noteInput,
            state = state,
            actions = viewModel
        )
    }
}

private const val ARG_EXPENSE_ID = "expenseId"
private const val NO_EXPENSE_ID = -1L