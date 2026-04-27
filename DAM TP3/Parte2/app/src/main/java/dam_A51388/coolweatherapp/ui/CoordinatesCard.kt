package dam_A51388.coolweatherapp.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CoordinatesCard(
    latitude: Float,
    longitude: Float,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onUpdateButtonClick: () -> Unit
) {

    Card {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            OutlinedTextField(
                value = latitude.toString(),
                onValueChange = onLatitudeChange,
                label = { Text("Latitude") })

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = longitude.toString(),
                onValueChange = onLongitudeChange,
                label = { Text("Longitude") })

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = onUpdateButtonClick) {
                Text("Update Weather")
            }
        }
    }
}


