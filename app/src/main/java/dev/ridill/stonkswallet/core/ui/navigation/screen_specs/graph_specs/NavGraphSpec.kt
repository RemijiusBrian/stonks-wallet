package dev.ridill.stonkswallet.core.ui.navigation.screen_specs.graph_specs

import dev.ridill.stonkswallet.core.ui.navigation.screen_specs.ScreenSpec

sealed interface NavGraphSpec {

    companion object {
        val allGraphs = listOf<NavGraphSpec>(
            ExpenseGraphSpec,
            PaymentPlanGraphSpec,
            SettingsGraphSpec
        ).associateBy(NavGraphSpec::graphRoute)
    }

    val graphRoute: String

    val children: List<ScreenSpec>
}