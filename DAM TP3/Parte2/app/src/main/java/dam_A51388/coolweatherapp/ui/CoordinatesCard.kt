package dam_A51388.coolweatherapp.ui

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dam_A51388.coolweatherapp.R
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public

@Composable
fun CoordinatesCard(
    isVisible: Boolean,
    initialLat: Float,
    initialLon: Float,
    onUpdateLocation: (Float, Float) -> Unit,
    onClose: () -> Unit,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>? = null
) {
    var latText by remember(initialLat) { mutableStateOf(initialLat.toString()) }
    var lonText by remember(initialLon) { mutableStateOf(initialLon.toString()) }
    val context = LocalContext.current

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
                    label = { Text(stringResource(R.string.latitude)) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = lonText,
                    onValueChange = { lonText = it },
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.longitude)) },
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
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.atualizar_localiza_o))
            }

            IconButton(
                onClick = {
                    val intent = Intent(context, LocationPickerActivity::class.java)
                    launcher?.launch(intent)
                }) {
                Icon(
                    imageVector = Icons.Filled.Public,
                    contentDescription = "Mapa"
                )
            }
        }
    }
}
