package dev.ridill.stonkswallet.core.ui.navigation.screen_specs

import androidx.compose.ui.graphics.vector.ImageVector
import dev.ridill.stonkswallet.core.ui.navigation.screen_specs.graph_specs.NavGraphSpec

sealed interface BottomBarScreenSpec : ScreenSpec {

    companion object {
        val screens: List<BottomBarScreenSpec> = NavGraphSpec.allGraphs.values
            .map { it.children }
            .flatten()
            .filterIsInstance<BottomBarScreenSpec>()
    }

    val icon: ImageVector
}