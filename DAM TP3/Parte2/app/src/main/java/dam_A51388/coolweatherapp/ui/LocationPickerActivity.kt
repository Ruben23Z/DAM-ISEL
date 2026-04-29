package dam_A51388.coolweatherapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

class LocationPickerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val lisboa = LatLng(38.7223, -9.1393)

            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(lisboa, 10f)
            }

            var markerPos by remember { mutableStateOf<LatLng?>(null) }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLngClicked ->
                    markerPos = latLngClicked
                    
                    val intent = Intent()
                    intent.putExtra("lat", markerPos?.latitude)
                    intent.putExtra("lon", markerPos?.longitude)

                    setResult(RESULT_OK, intent)
                    finish()
                }) {
                markerPos?.let {

                    Marker(state = MarkerState(position = it))
                }

            }
        }
    }
}
