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
import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dam_A51388.coolweatherapp.R

@Composable
fun WeatherUI(weatherViewModel: WeatherViewModel = viewModel()) {
    val weatherUIState by weatherViewModel.uiState.collectAsState()
    val latitude = weatherUIState.latitude
    val longitude = weatherUIState.longitude
    val temperature = weatherUIState.temperature
    val windSpeed = weatherUIState.windspeed
    val windDirection = weatherUIState.winddirection
    val weathercode = weatherUIState.weathercode
    val seaLevelPressure = weatherUIState.seaLevelPressure
    val time = weatherUIState.time
    
    val configuration = LocalConfiguration.current
    val day = weatherUIState.isDay == 1
    
    val mapt = getWeatherCodeMap()
    val wCode = mapt.get(weathercode)
    val wImage = when (wCode) {
        WMO_WeatherCode.CLEAR_SKY,
        WMO_WeatherCode.MAINLY_CLEAR,
        WMO_WeatherCode.PARTLY_CLOUDY -> if (day) wCode.image + "day"
        else wCode.image + "night"
        else -> wCode?.image ?: "mostly_cloudy"
    }
    
    val context = LocalContext.current
    val wIcon = context.resources.getIdentifier(wImage, "drawable", context.packageName)
    
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(weatherUIState.errorResId) {
        weatherUIState.errorResId?.let { resId ->
            snackbarHostState.showSnackbar(context.getString(resId))
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            if (weatherUIState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF378ADD))
                }
            } else {
                if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    LandscapeWeatherUI(
                        wIcon, latitude, longitude, temperature, windSpeed, windDirection, weathercode, seaLevelPressure, time,
                        onLatitudeChange = { newValue ->
                            newValue.toFloatOrNull()?.let { weatherViewModel.updateLatitude(it) }
                        },
                        onLongitudeChange = { newValue ->
                            newValue.toFloatOrNull()?.let { weatherViewModel.updateLongitude(it) }
                        },
                        onUpdateButtonClick = { weatherViewModel.fetchWeather() },
                        weatherData = weatherUIState.weatherData
                    )
                } else {
                    PortraitWeatherUI(
                        wIcon, latitude, longitude, temperature, windSpeed, windDirection, weathercode, seaLevelPressure, time,
                        onLatitudeChange = { newValue ->
                            newValue.toFloatOrNull()?.let { weatherViewModel.updateLatitude(it) }
                        },
                        onLongitudeChange = { newValue ->
                            newValue.toFloatOrNull()?.let { weatherViewModel.updateLongitude(it) }
                        },
                        onUpdateButtonClick = { weatherViewModel.fetchWeather() },
                        weatherData = weatherUIState.weatherData
                    )
                }
            }
        }
    }
}

@Composable
fun PortraitWeatherUI(
    wIcon: Int,
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
    onUpdateButtonClick: () -> Unit,
    weatherData: WeatherData?
) {
    if (weatherData != null) {
        WeatherContent(data = weatherData, onUpdateLocation = { lat, lon ->
            onLatitudeChange(lat.toString())
            onLongitudeChange(lon.toString())
            onUpdateButtonClick()
        })
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Nenhum dado disponível")
        }
    }
}

@Composable
fun LandscapeWeatherUI(
    wIcon: Int,
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
    onUpdateButtonClick: () -> Unit,
    weatherData: WeatherData?
) {
    if (weatherData != null) {
        WeatherContent(data = weatherData, onUpdateLocation = { lat, lon ->
            onLatitudeChange(lat.toString())
            onLongitudeChange(lon.toString())
            onUpdateButtonClick()
        })
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.nenhum_dado_dispon_vel))
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
        item { WeatherCard(data = data, onUpdateLocation = onUpdateLocation) }
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
