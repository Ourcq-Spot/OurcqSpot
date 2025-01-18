package com.ourcqspot.client

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.ourcqspot.client.graphs.Graph

sealed class BottomBarScreen(
    val route: String,
    val fr_title: String,
    val en_title: String,
    val icon: ImageVector,
    val onClick: (navController: NavHostController?) -> Unit
) {
    companion object {
        val SCREENS = listOf(
            Home,
            Agenda,
            Map,
            Points,
            Account
        )
        var lastScreen = SCREENS.first()

        fun checkIfDestinationRouteIsBefore(destinationRoute: String): Boolean {
            for (screen in SCREENS) {
                when (screen.route) {
                    lastScreen.route -> {
                        return false
                    }
                    destinationRoute -> {
                        return true
                    }
                    else -> {}
                }
            }
            return false
        }
    }

    object Home : BottomBarScreen(
        route = "HOME",
        fr_title = "Accueil",
        en_title = "Home",
        icon = Icons.AutoMirrored.Filled.List, //icon = Icons.AutoMirrored.Filled.List
        onClick = fun(navController: NavHostController?) {
            Log.d("hey", "ho1")
            navController?.navigate(Graph.DETAILS)
            Log.d("hey", "ho2")
        }
    )

    object Agenda : BottomBarScreen(
        route = "AGENDA",
        fr_title = "Agenda",
        en_title = "Agenda",
        icon = Icons.Default.DateRange,
        onClick = {}
    )

    object Map : BottomBarScreen(
        route = "MAP",
        fr_title = "Carte",
        en_title = "Map",
        icon = Icons.Default.LocationOn,
        onClick = {}
    )

    // TEST --- should be enabled in companion and in HomeScreen as well
    object Points : BottomBarScreen(
        route = "POINTS",
        fr_title = "Points",
        en_title = "Points",
        icon = Icons.Default.Star,
        onClick = {}
    )

    object Account : BottomBarScreen(
        route = "ACCOUNT",
        fr_title = "Compte",
        en_title = "Account",
        icon = Icons.Default.AccountCircle,
        onClick = {}
    )
}
