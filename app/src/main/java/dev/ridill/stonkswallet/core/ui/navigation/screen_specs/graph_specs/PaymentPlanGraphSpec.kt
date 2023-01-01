package dev.ridill.stonkswallet.core.ui.navigation.screen_specs.graph_specs

import dev.ridill.stonkswallet.core.ui.navigation.screen_specs.AddEditPaymentPlanScreenSpec
import dev.ridill.stonkswallet.core.ui.navigation.screen_specs.PaymentPlansScreenSpec
import dev.ridill.stonkswallet.core.ui.navigation.screen_specs.ScreenSpec

object PaymentPlanGraphSpec : NavGraphSpec {

    override val graphRoute: String = "bills_graph"

    override val children: List<ScreenSpec>
        get() = listOf(
            PaymentPlansScreenSpec,
            AddEditPaymentPlanScreenSpec
        )
}