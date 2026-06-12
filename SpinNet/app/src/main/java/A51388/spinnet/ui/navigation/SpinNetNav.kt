package A51388.spinnet.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import A51388.spinnet.ui.auth.AuthViewModel
import A51388.spinnet.ui.auth.LoginScreen
import A51388.spinnet.ui.auth.RegisterScreen
import A51388.spinnet.ui.dashboard.DashboardScreen
import A51388.spinnet.ui.planner.RoutinePlannerScreen
import A51388.spinnet.ui.planner.MyRoutinesScreen
import A51388.spinnet.ui.planner.RoutineViewModel
import A51388.spinnet.ui.planner.TrainingSessionScreen
import A51388.spinnet.ui.community.CommunityFeedScreen
import A51388.spinnet.ui.profile.PerformanceProfileScreen
import A51388.spinnet.ui.profile.PlayerProfileScreen

enum class SpinNetDestination(val route: String) {
    Login("login"),
    Register("register"),
    Dashboard("dashboard"),
    RoutinePlanner("routine_planner"),
    Performance("performance"),
    Community("community"),
    PlayerProfile("player_profile"),
    MyRoutines("my_routines"),
    TrainingSession("training_session")
}

@Composable
fun SpinNetNavHost(
    navController: NavHostController,
    currentDestination: SpinNetDestination,
    onNavigate: (SpinNetDestination) -> Unit,
    startDestination: SpinNetDestination,
    authViewModel: AuthViewModel,
    routineViewModel: RoutineViewModel
) {
    NavHost(
        navController = navController, startDestination = startDestination.route
    ) {
        composable(SpinNetDestination.Login.route) {
            LoginScreen(
                onLoginSuccess = { onNavigate(SpinNetDestination.Dashboard) },
                onNavigateToRegister = { onNavigate(SpinNetDestination.Register) })
        }
        composable(SpinNetDestination.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { onNavigate(SpinNetDestination.Dashboard) },
                onNavigateToLogin = { onNavigate(SpinNetDestination.Login) })
        }
        composable(SpinNetDestination.Dashboard.route) {
            DashboardScreen(
                currentDestination = currentDestination,
                onNavigate = onNavigate,
                routineViewModel = routineViewModel
            )
        }
        composable(SpinNetDestination.RoutinePlanner.route) {
            RoutinePlannerScreen(
                currentDestination = currentDestination,
                onNavigate = onNavigate,
                routineViewModel = routineViewModel
            )
        }
        composable(SpinNetDestination.Performance.route) {
            PerformanceProfileScreen(
                currentDestination = currentDestination, onNavigate = onNavigate
            )
        }
        composable(SpinNetDestination.Community.route) {
            CommunityFeedScreen(
                currentDestination = currentDestination,
                onNavigate = onNavigate,
                routineViewModel = routineViewModel
            )
        }
        composable(SpinNetDestination.PlayerProfile.route) {
            PlayerProfileScreen(
                currentDestination = currentDestination, onNavigate = onNavigate, onLogout = {
                    authViewModel.signOut()
                    onNavigate(SpinNetDestination.Login)
                })
        }
        composable(SpinNetDestination.MyRoutines.route) {
            MyRoutinesScreen(
                currentDestination = currentDestination,
                onNavigate = onNavigate,
                viewModel = routineViewModel
            )
        }
        composable(SpinNetDestination.TrainingSession.route) {
            TrainingSessionScreen(
                onNavigate = onNavigate,
                routineViewModel = routineViewModel
            )
        }
    }
}
