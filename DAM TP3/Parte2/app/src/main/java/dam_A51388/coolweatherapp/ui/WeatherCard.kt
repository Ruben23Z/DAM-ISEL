package dam_A51388.coolweatherapp.ui

import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WeatherCard(
    temperature: Float,
    windSpeed: Float,
    windDirection: Int,
    weathercode: Int,
    seaLevelPressure: Float,
    time: String
) {

    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            WeatherRow("Temperature", "$temperature ºC")
            WeatherRow("Wind Speed", "$windSpeed km/h")
            WeatherRow("Wind Direction", "$windDirection º")
            WeatherRow("Weather Code", "$weathercode")
            WeatherRow("Pressure", "$seaLevelPressure hPa")
            WeatherRow("Time", time)
        }
    }
}