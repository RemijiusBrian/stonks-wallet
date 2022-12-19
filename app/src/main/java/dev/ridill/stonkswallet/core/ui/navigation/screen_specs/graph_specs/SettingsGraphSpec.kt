package dev.ridill.stonkswallet.core.ui.navigation.screen_specs.graph_specs

import dev.ridill.stonkswallet.core.ui.navigation.screen_specs.ScreenSpec
import dev.ridill.stonkswallet.core.ui.navigation.screen_specs.SettingsScreenSpec

object SettingsGraphSpec : NavGraphSpec {

    override val graphRoute: String = "settings_graph"

    override val children: List<ScreenSpec>
        get() = listOf(
            SettingsScreenSpec
        )
}