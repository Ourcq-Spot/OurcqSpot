package com.ourcqspot.client.graphs

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.ourcqspot.client.BottomBarScreen
import com.ourcqspot.client.BottomBarScreen.Companion.checkIfDestinationRouteIsBefore
import com.ourcqspot.client.screens.ScreenContent

@Composable
fun HomeNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = BottomBarScreen.Map.route,
        enterTransition = {
            var towardsDirection = AnimatedContentTransitionScope.SlideDirection.Right
            if ( initialState.destination.route?.let {
                    checkIfDestinationRouteIsBefore(it)
                } == true) {
                towardsDirection = AnimatedContentTransitionScope.SlideDirection.Left
            }
            slideIntoContainer(
                towards = towardsDirection,
                animationSpec = tween(700)
            )
        },
        exitTransition = {
            var towardsDirection = AnimatedContentTransitionScope.SlideDirection.Right
            if ( initialState.destination.route?.let {
                    checkIfDestinationRouteIsBefore(it)
                } == true) {
                towardsDirection = AnimatedContentTransitionScope.SlideDirection.Left
            }
            slideOutOfContainer(
                towards = towardsDirection,
                animationSpec = tween(700)
            )
        },
        /*popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(700)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(700)
            )
            /*when (initialState.destination.route) {
                BottomBarScreen.SCREENS.first().route,
                BottomBarScreen.SCREENS.last().route
                    -> towardsDirection = AnimatedContentTransitionScope.SlideDirection.Right
                //else -> {} // ExitTransition.None
            }*/
        }*/
    ) {
        composable(route = BottomBarScreen.SCREENS[0].route) {
            ScreenContent (
                name = BottomBarScreen.SCREENS[0].route,
                useHypertextStyle = true,
                onClick = {
                    navController.navigate(Graph.DETAILS)
                }
            )
        }
        composable(route = BottomBarScreen.SCREENS[1].route) {
            ScreenContent(
                name = BottomBarScreen.SCREENS[1].route,
                onClick = { }
            )
        }
        composable(route = BottomBarScreen.SCREENS[2].route) {
            ScreenContent(
                name = BottomBarScreen.SCREENS[2].route,
                onClick = { }
            )
        }
        composable(route = BottomBarScreen.SCREENS[3].route) {
            ScreenContent(
                name = BottomBarScreen.SCREENS[3].route,
                onClick = { }
            )
        }
        composable(route = BottomBarScreen.SCREENS[4].route) {
            ScreenContent(
                name = BottomBarScreen.SCREENS[4].route,
                onClick = { }
                //onClick = BottomBarScreen.SCREENS[4].onClick
            )
        }
        detailsNavGraph(navController = navController)
    }
}

fun NavGraphBuilder.detailsNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.DETAILS,
        startDestination = DetailsScreen.Information.route
    ) {
        composable(route = DetailsScreen.Information.route) {
            ScreenContent(name = DetailsScreen.Information.route) {
                navController.navigate(DetailsScreen.Overview.route)
            }
        }
        composable(route = DetailsScreen.Overview.route) {
            ScreenContent(name = DetailsScreen.Overview.route) {
                navController.popBackStack(
                    route = DetailsScreen.Information.route,
                    inclusive = false
                )
            }
        }
    }
}

sealed class DetailsScreen(val route: String) {
    object Information : DetailsScreen(route = "INFORMATION")
    object Overview : DetailsScreen(route = "OVERVIEW")
}
