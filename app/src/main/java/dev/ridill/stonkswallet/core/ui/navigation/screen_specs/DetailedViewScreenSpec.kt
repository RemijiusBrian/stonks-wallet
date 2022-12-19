package dev.ridill.stonkswallet.core.ui.navigation.screen_specs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import dev.ridill.stonkswallet.R
import dev.ridill.stonkswallet.core.ui.components.rememberSnackbarController
import dev.ridill.stonkswallet.core.util.exhaustive
import dev.ridill.stonkswallet.feature_expenses.presentation.detailed_view.DetailedViewScreen
import dev.ridill.stonkswallet.feature_expenses.presentation.detailed_view.DetailedViewState
import dev.ridill.stonkswallet.feature_expenses.presentation.detailed_view.DetailedViewViewModel

object DetailedViewScreenSpec : BottomBarScreenSpec {

    override val icon: ImageVector = Icons.Outlined.CalendarMonth

    override val label: Int = R.string.destination_detailed_view

    override val navHostRoute: String = "detailed_view"

    @Composable
    override fun Content(navController: NavController, navBackStackEntry: NavBackStackEntry) {
        val viewModel: DetailedViewViewModel = hiltViewModel(navBackStackEntry)
        val state by viewModel.state.observeAsState(DetailedViewState.INITIAL)

        val snackbarController = rememberSnackbarController()
        val context = LocalContext.current

        LaunchedEffect(viewModel, context) {
            viewModel.events.collect { event ->
                when (event) {
                    is DetailedViewViewModel.DetailedViewEvent.ShowUiMessage -> {
                        snackbarController.showSnackbar(
                            event.message.asString(context),
                            event.isError
                        )
                    }
                }.exhaustive
            }
        }

        DetailedViewScreen(
            snackbarController = snackbarController,
            state = state,
            actions = viewModel,
            navigateUp = navController::popBackStack
        )
    }
}