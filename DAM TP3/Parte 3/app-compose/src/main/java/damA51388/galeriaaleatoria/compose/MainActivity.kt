package damA51388.galeriaaleatoria.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import damA51388.galeriaaleatoria.compose.ui.DogFeedScreen
import damA51388.galeriaaleatoria.compose.ui.theme.DogFeedTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DogFeedTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DogFeedScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
