package com.yunusek.qrguard.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yunusek.qrguard.R
import com.yunusek.qrguard.ui.screens.create.CreateScreen
import com.yunusek.qrguard.ui.screens.favorites.FavoritesScreen
import com.yunusek.qrguard.ui.screens.history.HistoryScreen
import com.yunusek.qrguard.ui.screens.home.HomeScreen
import com.yunusek.qrguard.ui.screens.scan.ScanScreen
import com.yunusek.qrguard.ui.screens.settings.SettingsScreen
import com.yunusek.qrguard.ui.theme.AccentBlue
import com.yunusek.qrguard.ui.theme.CardBackground
import com.yunusek.qrguard.ui.theme.GradientStart
import com.yunusek.qrguard.ui.theme.TextMuted
import com.yunusek.qrguard.ui.theme.TextPrimary

sealed class Screen(
    val route: String,
    val labelResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : Screen(
        route = "home",
        labelResId = R.string.nav_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
    data object Scan : Screen(
        route = "scan",
        labelResId = R.string.nav_scan,
        selectedIcon = Icons.Filled.QrCodeScanner,
        unselectedIcon = Icons.Outlined.QrCodeScanner
    )
    data object Create : Screen(
        route = "create",
        labelResId = R.string.nav_create,
        selectedIcon = Icons.Filled.Add,
        unselectedIcon = Icons.Outlined.Add
    )
    data object History : Screen(
        route = "history",
        labelResId = R.string.nav_history,
        selectedIcon = Icons.Filled.History,
        unselectedIcon = Icons.Outlined.History
    )
    data object Favorites : Screen(
        route = "favorites",
        labelResId = R.string.favorites,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
    data object Settings : Screen(
        route = "settings",
        labelResId = R.string.settings,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Scan,
    Screen.Create,
    Screen.History
)

private fun NavController.navigateToBottomNavDestination(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

private fun NavController.navigateToScreen(route: String) {
    navigate(route) {
        launchSingleTop = true
    }
}

@Composable
fun QrGuardNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.route }

    Scaffold(
        modifier = modifier,
        containerColor = GradientStart,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = CardBackground,
                    contentColor = TextPrimary
                ) {
                    bottomNavItems.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = stringResource(screen.labelResId)
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(screen.labelResId),
                                    maxLines = 1
                                )
                            },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AccentBlue,
                                selectedTextColor = AccentBlue,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted,
                                indicatorColor = AccentBlue.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onScanClick = {
                        navController.navigateToBottomNavDestination(Screen.Scan.route)
                    },
                    onCreateClick = {
                        navController.navigateToBottomNavDestination(Screen.Create.route)
                    },
                    onHistoryClick = {
                        navController.navigateToBottomNavDestination(Screen.History.route)
                    },
                    onFavoritesClick = {
                        navController.navigateToScreen(Screen.Favorites.route)
                    },
                    onSettingsClick = {
                        navController.navigateToScreen(Screen.Settings.route)
                    }
                )
            }

            composable(Screen.Scan.route) {
                ScanScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Create.route) {
                CreateScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}