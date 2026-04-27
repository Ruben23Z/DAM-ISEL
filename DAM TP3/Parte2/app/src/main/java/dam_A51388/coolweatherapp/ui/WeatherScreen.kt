package dam_A51388.coolweatherapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dam_A51388.coolweatherapp.data.*
import dam_A51388.coolweatherapp.ui.theme.WeatherAppTheme
import dam_A51388.coolweatherapp.viewmodel.WeatherViewModel

@Composable
fun WeatherUI(viewModel: WeatherViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is WeatherUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF378ADD))
                    }
                }
                is WeatherUiState.Success -> {
                    WeatherContent(
                        data = state.data,
                        onUpdateLocation = { lat, lon -> viewModel.fetchWeather(lat, lon) }
                    )
                }
                is WeatherUiState.Error -> {
                    LaunchedEffect(state.message) {
                        snackbarHostState.showSnackbar(state.message)
                    }
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherContent(data: WeatherData, onUpdateLocation: (Float, Float) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(10.dp)
    ) {
        item { HeroCard(data = data, onUpdateLocation = onUpdateLocation) }
        item { QuickStatsGrid(data = data) }
        item { HourlyForecastCard(data = data) }
        item { SunCard(data = data) }
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun WeatherPreviewLight() {
    val mockData = getMockWeatherData()
    WeatherAppTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            WeatherContent(data = mockData, onUpdateLocation = { _, _ -> })
        }
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun WeatherPreviewDark() {
    val mockData = getMockWeatherData()
    WeatherAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            WeatherContent(data = mockData, onUpdateLocation = { _, _ -> })
        }
    }
}

private fun getMockWeatherData() = WeatherData(
    latitude = 38.7169f,
    longitude = -9.1395f,
    timezone = "Europe/Lisbon",
    utcOffset = 3600,
    current = CurrentWeather(
        time = "2026-04-27T12:00",
        temperature = 23.5f,
        apparentTemperature = 25.0f,
        weatherCode = 1,
        isDay = 1,
        windSpeed = 12.5f,
        windDirection = 214,
        surfacePressure = 1013.5f
    ),
    hourly = HourlyWeather(
        time = List(24) { "2026-04-27T%02d:00".format(it) },
        temperatures = listOf(20f, 19f, 18f, 18f, 17f, 17f, 18f, 19f, 21f, 23f, 24f, 25f, 26f, 26f, 25f, 24f, 23f, 22f, 21f, 20f, 19f, 18f, 18f, 17f),
        weatherCodes = List(24) { 1 },
        precipitationProbability = List(24) { if (it > 15) 10 else 0 },
        windSpeeds = List(24) { 10f },
        pressures = List(24) { 1013f }
    ),
    daily = DailyWeather(
        time = listOf("2026-04-27"),
        sunrise = listOf("2026-04-27T06:30"),
        sunset = listOf("2026-04-27T20:30"),
        uvIndexMax = listOf(6.5f)
    )
)
