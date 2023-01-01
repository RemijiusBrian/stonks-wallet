package dev.ridill.stonkswallet.core.ui.navigation.screen_specs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import dev.ridill.stonkswallet.R

object PaymentPlansScreenSpec : BottomBarScreenSpec {

    override val icon: ImageVector = Icons.Outlined.ReceiptLong

    override val label: Int = R.string.destination_payment_plans

    override val navHostRoute: String = "payment_plans"

    @Composable
    override fun Content(navController: NavController, navBackStackEntry: NavBackStackEntry) {
    }
}