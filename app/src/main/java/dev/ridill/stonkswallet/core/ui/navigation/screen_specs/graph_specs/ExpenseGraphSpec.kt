package dev.ridill.stonkswallet.core.ui.navigation.screen_specs.graph_specs

import dev.ridill.stonkswallet.core.ui.navigation.screen_specs.AddEditExpenseScreenSpec
import dev.ridill.stonkswallet.core.ui.navigation.screen_specs.DetailedViewScreenSpec
import dev.ridill.stonkswallet.core.ui.navigation.screen_specs.ScreenSpec

object ExpenseGraphSpec : NavGraphSpec {

    override val graphRoute: String = "expense_graph"

    override val children: List<ScreenSpec>
        get() = listOf(
            DetailedViewScreenSpec,
            AddEditExpenseScreenSpec
        )
}