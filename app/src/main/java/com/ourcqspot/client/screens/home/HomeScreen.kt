package com.ourcqspot.client.screens.home

import android.widget.Space
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomAppBar
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigation
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigationItem
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ContentAlpha
import androidx.compose.material.TextField
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ourcqspot.client.BottomBarScreen
import com.ourcqspot.client.R
import com.ourcqspot.client.graphs.HomeNavGraph
import com.ourcqspot.client.ui.theme.NUNITO_FONT
import com.ourcqspot.client.ui.theme.SelectedBottomItemColor
import com.ourcqspot.client.ui.theme.UnselectedBottomItemColor

// @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavHostController = rememberNavController()) {
    val focusManager = LocalFocusManager.current
    Scaffold (
        modifier = Modifier.clickable { focusManager.clearFocus() },
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
                .imePadding()
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

    TopAppBar (
        modifier = Modifier
            .statusBarsPadding()
            .defaultMinSize(minHeight = 120.dp)
            .fillMaxWidth(),
        backgroundColor = Color(0xffffffff)
        //contentColor = Color(0xff0E2176)
    ) {
        Box (
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 5.dp, vertical = 12.5.dp)
        ) {
            Column(
                modifier = Modifier
                    //.background(color = Color(0xff0088ff))
                    .padding(horizontal = 5.dp)
                    .defaultMinSize(minHeight = 30.dp, minWidth = 10.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 7.5.dp)
                        //.background(color = Color(0xffff0000))
                        //.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(R.drawable.logo_ourcqspot),
                        contentDescription = stringResource(id = R.string.logo_ourcqspot_content_description),
                        modifier = Modifier.height(height = 20.dp)
                    )
                }
                Row (
                    modifier = Modifier//.background(color = Color.Blue)
                        .padding(vertical = 7.5.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val searchPlaceholder = "Recherche"
                    var searchValue by remember { mutableStateOf("") }
                    var placeholderAlpha by remember { mutableStateOf(1F) }
                    //var searchValue by remember { mutableStateOf(searchPlaceholder) }
                    val focusManager = LocalFocusManager.current
                    Row (
                        modifier = Modifier
                            .clickable { focusManager.moveFocus(FocusDirection.Next) }
                            .weight(1f)
                            .alpha(.5F)
                            .border(
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = Color(0xff000000)
                                ),
                                shape = RoundedCornerShape(50.dp)
                            )
                            .padding(horizontal = 20.dp)
                            //.width(width = 300.dp)
                            .height(height = 50.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon (
                            painter = painterResource(R.drawable.icon_search),
                            contentDescription = "",
                            modifier = Modifier.size(size = 16.6.dp)
                        )
                        Spacer ( modifier = Modifier.width(width = 10.dp) )
                        Box (
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text (
                                searchPlaceholder,
                                fontFamily = NUNITO_FONT,
                                fontSize = 17.5.sp,
                                modifier = Modifier.alpha(placeholderAlpha)
                            )
                            BasicTextField(
                                value = searchValue,
                                onValueChange = { searchValue = it },
                                textStyle = TextStyle(
                                    fontFamily = NUNITO_FONT,
                                    fontSize = 17.5.sp
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Search
                                ),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        focusManager.clearFocus()
                                    },
                                    onNext = {
                                        focusManager.moveFocus(FocusDirection.Down)
                                    },
                                    //onDone = {}
                                ),

                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged {
                                        placeholderAlpha = if ((!it.isFocused) and (searchValue=="")) {
                                            1F
                                        } else {
                                            0F
                                        }
                                    }
                            )
                        }
                    }
                    Spacer ( modifier = Modifier.width(width = 7.5.dp) )
                    Image (
                        painter = painterResource(R.drawable.icon_filters),
                        contentDescription = "",
                        //tint = Color(0xff0E2176),
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SearchIcon() {
    Icon(
        painter = painterResource(R.drawable.icon_search),
        contentDescription = ""
    )
}

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = BottomBarScreen.SCREENS
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    BottomAppBar (
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding()
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
                color = itemColor,
                fontFamily = NUNITO_FONT
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