package A51388.spinnet

import A51388.spinnet.ui.auth.AuthViewModel
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import A51388.spinnet.ui.navigation.SpinNetDestination
import A51388.spinnet.ui.navigation.SpinNetNavHost
import A51388.spinnet.ui.theme.SpinNetTheme
import android.Manifest
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel
import com.facebook.CallbackManager

class MainActivity : ComponentActivity() {
    val callbackManager: CallbackManager = CallbackManager.Factory.create()
    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }



        enableEdgeToEdge()
        setContent {
            SpinNetTheme {
                SpinNetApp()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}

@Composable
fun SpinNetApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    val startDestination = if (authViewModel.isLoggedIn()) SpinNetDestination.Dashboard
    else SpinNetDestination.Login

    var currentDestination by remember { mutableStateOf(startDestination) }

    val onNavigate = { destination: SpinNetDestination ->
        currentDestination = destination
        navController.navigate(destination.route) {
            popUpTo(navController.graph.startDestinationId) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    SpinNetNavHost(
        navController = navController,
        currentDestination = currentDestination,
        onNavigate = onNavigate,
        startDestination = startDestination,
        authViewModel = authViewModel
    )
}