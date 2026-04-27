package dam_A51388.coolweatherapp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CoordinatesCard(
    isVisible: Boolean,
    initialLat: Float,
    initialLon: Float,
    onUpdateLocation: (Float, Float) -> Unit,
    onClose: () -> Unit
) {
    var latText by remember(initialLat) { mutableStateOf(initialLat.toString()) }
    var lonText by remember(initialLon) { mutableStateOf(initialLon.toString()) }

    AnimatedVisibility(visible = isVisible) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = latText,
                    onValueChange = { latText = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Lat") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = lonText,
                    onValueChange = { lonText = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Lon") },
                    singleLine = true
                )
            }
            Button(
                onClick = {
                    val lat = latText.toFloatOrNull()
                    val lon = lonText.toFloatOrNull()
                    if (lat != null && lon != null) {
                        onUpdateLocation(lat, lon)
                        onClose()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Atualizar localização")
            }
        }
    }
}
