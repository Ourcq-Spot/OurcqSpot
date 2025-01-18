package com.ourcqspot.client.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomAppBar
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigation
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigationItem
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ourcqspot.client.BottomBarScreen
import com.ourcqspot.client.R
import com.ourcqspot.client.graphs.HomeNavGraph
import com.ourcqspot.client.ui.theme.SelectedBottomItemColor
import com.ourcqspot.client.ui.theme.UnselectedBottomItemColor

// @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavHostController = rememberNavController()) {
    Scaffold (
        topBar = {
            TopBar(navController = navController)
        },
        bottomBar = {
            BottomBar(navController = navController)
        },
        //contentWindowInsets =
    ) { innerPadding ->
        Column (
            modifier = Modifier//.padding(innerPadding)
                .navigationBarsPadding()
                .padding(PaddingValues(top = innerPadding.calculateTopPadding()))
                .fillMaxSize()
                .background(color = Color.Yellow),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HomeNavGraph(navController = navController)
        }
    }
}

@Composable
fun TopBar(navController: NavHostController) {
    val screens = BottomBarScreen.SCREENS
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    androidx.compose.material.TopAppBar (
        modifier = Modifier
            .statusBarsPadding()
            //.defaultMinSize(minHeight = 75.dp)
            .fillMaxWidth(),
        backgroundColor = Color(0xffff00ff)
        //contentColor = Color(0xff0E2176)
    ) {
        Row (
            modifier = Modifier.background( color = Color(0xffff0000) )
                .defaultMinSize(minHeight = 30.dp, minWidth = 10.dp)
                //.fillMaxSize()
        ) {
            Image(
                painter = painterResource(R.drawable.logo_ourcqspot),
                contentDescription = stringResource(id = R.string.logo_ourcqspot_content_description),
                //modifier = Modifier.height(height = 20.dp)
            )
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = BottomBarScreen.SCREENS
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    BottomAppBar (
        modifier = Modifier
            .navigationBarsPadding()
            .padding(vertical = 30.dp, horizontal = 27.5.dp)
            .clip(RoundedCornerShape(20.dp))
            .defaultMinSize(minHeight = 75.dp),
        //contentColor = Color(0xff0E2176)
        backgroundColor = Color(0xff0E2176)
    ) {
        val bottomBarDestination = screens.any { it.route == currentDestination?.route }
        if (bottomBarDestination) {
            BottomNavigation (
                //modifier = Modifier//.size(width = 300.dp/*, height = 100.dp*/)
                    //.width(width = 300.dp),
                backgroundColor = Color.Transparent,
                elevation = 0.dp
            ) {
                screens.forEach { screen ->
                    AddItem(
                        screen = screen,
                        currentDestination = currentDestination,
                        navController = navController,
                    )
                }
            }
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    var itemColor = UnselectedBottomItemColor
    if (currentDestination?.hierarchy?.any {
            it.route == screen.route
        } == true) {
        itemColor = SelectedBottomItemColor
    }
    BottomNavigationItem(
        label = {
            Text(
                screen.fr_title,
                color = itemColor
            )
        },
        icon = {
            Icon(
                imageVector = screen.icon,
                contentDescription = "Navigation Icon",
                tint = itemColor
            )
        },
        selected = currentDestination?.hierarchy?.any {
            it.route == screen.route
        } == true,
        unselectedContentColor = LocalContentColor.current.copy(alpha = ContentAlpha.disabled),
        onClick = {
            BottomBarScreen.lastScreen = screen // YYEYYSYSYYES SLIDING TRANSITION DO WORK!!!
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            }
        }
    )
}