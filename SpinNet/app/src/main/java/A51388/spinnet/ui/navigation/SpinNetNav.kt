package A51388.spinnet.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import A51388.spinnet.ui.screens.CommunityFeedScreen
import A51388.spinnet.ui.screens.DashboardScreen
import A51388.spinnet.ui.screens.PerformanceProfileScreen
import A51388.spinnet.ui.screens.RoutinePlannerScreen

enum class SpinNetDestination(val route: String) {
    Dashboard("dashboard"),
    RoutinePlanner("routine_planner"),
    Performance("performance"),
    Community("community")
}

@Composable
fun SpinNetNavHost(
    navController: NavHostController,
    currentDestination: SpinNetDestination,
    onNavigate: (SpinNetDestination) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = SpinNetDestination.Dashboard.route
    ) {
        composable(SpinNetDestination.Dashboard.route) {
            DashboardScreen(
                currentDestination = currentDestination,
                onNavigate = onNavigate
            )
        }
        composable(SpinNetDestination.RoutinePlanner.route) {
            RoutinePlannerScreen(
                currentDestination = currentDestination,
                onNavigate = onNavigate
            )
        }
        composable(SpinNetDestination.Performance.route) {
            PerformanceProfileScreen(
                currentDestination = currentDestination,
                onNavigate = onNavigate
            )
        }
        composable(SpinNetDestination.Community.route) {
            CommunityFeedScreen(
                currentDestination = currentDestination,
                onNavigate = onNavigate
            )
        }
    }
}
