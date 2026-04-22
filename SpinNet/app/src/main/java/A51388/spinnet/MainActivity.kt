package A51388.spinnet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import A51388.spinnet.ui.navigation.SpinNetDestination
import A51388.spinnet.ui.navigation.SpinNetNavHost
import A51388.spinnet.ui.theme.SpinNetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpinNetTheme {
                SpinNetApp()
            }
        }
    }
}

@Composable
fun SpinNetApp() {
    val navController = rememberNavController()
    var currentDestination by remember { mutableStateOf(SpinNetDestination.Dashboard) }

    val onNavigate: (SpinNetDestination) -> Unit = { destination ->
        currentDestination = destination
        navController.navigate(destination.route) {
            popUpTo(navController.graph.startDestinationId) { saveState = true }
            launchSingleTop = true
            restoreState    = true
        }
    }

    SpinNetNavHost(
        navController        = navController,
        currentDestination   = currentDestination,
        onNavigate           = onNavigate
    )
}