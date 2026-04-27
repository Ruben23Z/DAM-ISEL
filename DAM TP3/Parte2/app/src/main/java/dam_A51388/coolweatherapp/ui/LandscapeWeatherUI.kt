package dam_A51388.coolweatherapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LandscapeWeatherUI(
    latitude: Float,
    longitude: Float,
    temperature: Float,
    windSpeed: Float,
    windDirection: Int,
    weathercode: Int,
    seaLevelPressure: Float,
    time: String,

    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onUpdateButtonClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Column(modifier = Modifier.weight(1f)) {
            CoordinatesCard(
                latitude, longitude, onLatitudeChange, onLongitudeChange, onUpdateButtonClick
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            WeatherCard(
                temperature, windSpeed, windDirection, weathercode, seaLevelPressure, time
            )
        }
    }
}