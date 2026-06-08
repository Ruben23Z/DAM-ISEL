package dam_A51388.coolweatherapp.ui

import dam_A51388.coolweatherapp.data.FavoriteLocation
import dam_A51388.coolweatherapp.data.WeatherData


data class WeatherUiState(
    val latitude: Float = 0f,
    val longitude: Float = 0f,
    val temperature: Float = 0f,
    val windspeed: Float = 0f,
    val winddirection: Int = 0,
    val weathercode: Int = 0,
    val seaLevelPressure: Float = 0f,
    val time: String = "",
    val isDay: Int = 1,
    val isLoading: Boolean = false,
    val errorResId: Int? = null,
    val weatherData: dam_A51388.coolweatherapp.data.WeatherData? = null,
    val favorites: List<FavoriteLocation> = emptyList()
)
