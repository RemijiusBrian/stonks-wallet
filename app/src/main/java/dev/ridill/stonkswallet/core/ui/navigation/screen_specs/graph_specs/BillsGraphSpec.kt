package dev.ridill.stonkswallet.core.ui.navigation.screen_specs.graph_specs

import dev.ridill.stonkswallet.core.ui.navigation.screen_specs.AddEditPaymentPlanScreenSpec
import dev.ridill.stonkswallet.core.ui.navigation.screen_specs.PaymentPlansListScreenSpec
import dev.ridill.stonkswallet.core.ui.navigation.screen_specs.ScreenSpec

object BillsGraphSpec : NavGraphSpec {

    override val graphRoute: String = "bills_graph"

    override val children: List<ScreenSpec>
        get() = listOf(
            PaymentPlansListScreenSpec,
            AddEditPaymentPlanScreenSpec
        )
}