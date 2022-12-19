package dev.ridill.stonkswallet.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import dev.ridill.stonkswallet.core.ui.navigation.screen_specs.DashboardScreenSpec
import dev.ridill.stonkswallet.core.ui.navigation.screen_specs.ScreenSpec
import dev.ridill.stonkswallet.core.ui.navigation.screen_specs.graph_specs.NavGraphSpec

@Composable
fun SWNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    require(NavGraphSpec.allGraphs.isNotEmpty()) {
        "NavGraph must contain at least 1 destination"
    }

    NavHost(
        navController = navController,
        startDestination = DashboardScreenSpec.navHostRoute,
        modifier = modifier
    ) {
        addScreen(navController, DashboardScreenSpec)

        NavGraphSpec.allGraphs.values.forEach { navGraphSpec ->
            addGraph(navController, navGraphSpec)
        }
    }
}

private fun NavGraphBuilder.addScreen(navController: NavHostController, screenSpec: ScreenSpec) {
    composable(
        route = screenSpec.navHostRoute,
        arguments = screenSpec.arguments,
        deepLinks = screenSpec.deepLinks
    ) { navBackStackEntry ->
        screenSpec.Content(
            navController = navController,
            navBackStackEntry = navBackStackEntry
        )
    }
}

private fun NavGraphBuilder.addGraph(navController: NavHostController, graphSpec: NavGraphSpec) {
    require(graphSpec.children.isNotEmpty()) {
        "Graph must have at least 1 destination"
    }
    navigation(
        route = graphSpec.graphRoute,
        startDestination = graphSpec.children.first().navHostRoute
    ) {
        graphSpec.children.forEach { screenSpec ->
            addScreen(navController, screenSpec)
        }
    }
}