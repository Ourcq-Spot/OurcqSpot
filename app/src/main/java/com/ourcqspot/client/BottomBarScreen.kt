package com.ourcqspot.client

import android.graphics.drawable.Icon
import android.util.Log
import androidx.navigation.NavHostController
import com.ourcqspot.client.graphs.Graph

sealed class BottomBarScreen(
    val route: String,
    val fr_title: String,
    val en_title: String,
    val icon_painter_id: Int,
    val icon_content_description: String,
    //val icon: Icon,
    // val icon: @Composable () -> Unit,
    val onClick: (navController: NavHostController?) -> Unit
) {
    companion object {
        val SCREENS = listOf(
            News,
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

    object News : BottomBarScreen(
        route = "NEWS",
        fr_title = "Actualités",
        en_title = "News",
        icon_painter_id = R.drawable.icon_news,
        icon_content_description = "Actualités",
        // icon = Icons.AutoMirrored.Filled.List, //icon = Icons.AutoMirrored.Filled.List
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
        icon_painter_id = R.drawable.icon_agenda,
        icon_content_description = "Agenda",
        // icon = Icons.Default.DateRange,
        onClick = {}
    )

    object Map : BottomBarScreen(
        route = "MAP",
        fr_title = "Carte",
        en_title = "Map",
        icon_painter_id = R.drawable.icon_map,
        icon_content_description = "Carte",
        // icon = Icons.Default.LocationOn,
        onClick = {}
    )

    // TEST --- should be enabled in companion and in HomeScreen as well
    object Points : BottomBarScreen(
        route = "POINTS",
        fr_title = "Points",
        en_title = "Points",
        icon_painter_id = R.drawable.icon_points,
        icon_content_description = "Points",
        // icon = Icons.Default.Star,
        onClick = {}
    )

    object Account : BottomBarScreen(
        route = "ACCOUNT",
        fr_title = "Compte",
        en_title = "Account",
        //icon = Icons.Default.AccountCircle,
        icon_painter_id = R.drawable.icon_account,
        icon_content_description = "Compte",
        /* icon = Icon(
            painter = painterResource(R.drawable.icon_account),
            contentDescription = this.fr_title
        ), */
        onClick = {}
    )
}
