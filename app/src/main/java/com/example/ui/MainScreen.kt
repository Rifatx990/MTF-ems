package com.example.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.AccountingScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.WorkersScreen

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val items = listOf(
                    Triple("Dashboard", DashboardRoute, Icons.Default.Dashboard),
                    Triple("Orders", OrdersRoute, Icons.Default.ShoppingCart),
                    Triple("Workers", WorkersRoute, Icons.Default.People),
                    Triple("Reports", ReportsRoute, Icons.Default.Assessment),
                    Triple("Accounting", AccountingRoute, Icons.Default.AccountBalance)
                )

                items.forEach { (name, route, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = name) },
                        label = { Text(name) },
                        selected = currentDestination?.hierarchy?.any { it.route == route::class.qualifiedName } == true,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = DashboardRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<DashboardRoute> { DashboardScreen(viewModel) }
            composable<OrdersRoute> { OrdersScreen(viewModel) }
            composable<WorkersRoute> { WorkersScreen(viewModel) }
            composable<ReportsRoute> { ReportsScreen(viewModel) }
            composable<AccountingRoute> { AccountingScreen(viewModel) }
        }
    }
}
