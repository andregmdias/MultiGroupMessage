package com.giannatech.multigroupmessage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.giannatech.multigroupmessage.ui.MainViewModel
import com.giannatech.multigroupmessage.ui.screens.AnalyticsScreen
import com.giannatech.multigroupmessage.ui.screens.FaqScreen
import com.giannatech.multigroupmessage.ui.screens.GroupsScreen
import com.giannatech.multigroupmessage.ui.screens.HomeScreen
import com.giannatech.multigroupmessage.ui.screens.SendMessageScreen
import com.giannatech.multigroupmessage.ui.screens.SettingsScreen
import com.giannatech.multigroupmessage.ui.theme.Flip7Background
import com.giannatech.multigroupmessage.ui.theme.Flip7Border
import com.giannatech.multigroupmessage.ui.theme.Flip7CyberGold
import com.giannatech.multigroupmessage.ui.theme.Flip7ElectricCyan
import com.giannatech.multigroupmessage.ui.theme.Flip7NeonGreen
import com.giannatech.multigroupmessage.ui.theme.Flip7Surface
import com.giannatech.multigroupmessage.ui.theme.Flip7TextMuted
import com.giannatech.multigroupmessage.ui.theme.MultiGroupMessageTheme

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Início", Icons.Default.Home)
    object Groups : Screen("groups", "Grupos", Icons.Default.Group)
    object SendMessage : Screen("send_message", "Enviar", Icons.Default.Send)
    object Analytics : Screen("analytics", "Métricas", Icons.Default.Analytics)
    object Settings : Screen("settings", "Ajustes", Icons.Default.Settings)
    object Faq : Screen("faq", "FAQ", Icons.Default.HelpOutline)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MultiGroupMessageTheme {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun MainAppScreen() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel(factory = MainViewModel.Factory(LocalContext.current))

    val items = listOf(
        Screen.Home,
        Screen.Groups,
        Screen.SendMessage,
        Screen.Analytics,
        Screen.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Flip7Background,
        bottomBar = {
            NavigationBar(
                containerColor = Flip7Surface,
                tonalElevation = 8.dp
            ) {
                items.forEach { screen ->
                    val selected = currentRoute == screen.route
                    val itemColor = when (screen) {
                        Screen.SendMessage -> Flip7NeonGreen
                        Screen.Groups -> Flip7ElectricCyan
                        Screen.Analytics -> Flip7CyberGold
                        else -> Flip7NeonGreen
                    }

                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
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
                            selectedIconColor = itemColor,
                            selectedTextColor = itemColor,
                            indicatorColor = itemColor.copy(alpha = 0.15f),
                            unselectedIconColor = Flip7TextMuted,
                            unselectedTextColor = Flip7TextMuted
                        )
                    )
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
                    viewModel = viewModel,
                    onNavigateToSendMessage = { navController.navigate(Screen.SendMessage.route) },
                    onNavigateToGroups = { navController.navigate(Screen.Groups.route) },
                    onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }
            composable(Screen.Groups.route) {
                GroupsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.SendMessage.route) {
                SendMessageScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Analytics.route) {
                AnalyticsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToFaq = { navController.navigate(Screen.Faq.route) }
                )
            }
            composable(Screen.Faq.route) {
                FaqScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
