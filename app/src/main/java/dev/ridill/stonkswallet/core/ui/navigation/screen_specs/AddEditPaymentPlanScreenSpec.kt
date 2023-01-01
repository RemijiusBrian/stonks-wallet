package dev.ridill.stonkswallet.core.ui.navigation.screen_specs

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import dev.ridill.stonkswallet.R

object AddEditPaymentPlanScreenSpec : ScreenSpec {

    override val label: Int = R.string.destination_add_edit_payment_plan

    override val navHostRoute: String = "add_edit_payment_plan"

    @Composable
    override fun Content(navController: NavController, navBackStackEntry: NavBackStackEntry) {
    }
}