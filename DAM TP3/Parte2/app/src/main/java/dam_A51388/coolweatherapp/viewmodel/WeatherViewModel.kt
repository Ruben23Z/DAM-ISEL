package dam_A51388.coolweatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam_A51388.coolweatherapp.data.FavoriteLocation
import dam_A51388.coolweatherapp.data.WeatherApiClient
import dam_A51388.coolweatherapp.ui.WeatherUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState(isLoading = true))
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()
    val listaEx = listOf(FavoriteLocation("Lisboa", 38.7169f, -9.1395f), FavoriteLocation("Porto",   41.1499f, -8.6109f))
    _uiState.update{ it.copy(favorites = listaDeExemplo) }
    private var currentLat = 38.7169f
    private var currentLon = -9.1395f

    init {
        fetchWeather()
    }

    fun updateLatitude(lat: Float) {
        currentLat = lat
        _uiState.update { it.copy(latitude = lat) }
    }

    fun updateLongitude(lon: Float) {
        currentLon = lon
        _uiState.update { it.copy(longitude = lon) }
    }

    fun fetchWeather(lat: Float = currentLat, lon: Float = currentLon) {
        currentLat = lat
        currentLon = lon
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorResId = null) }
            val result = WeatherApiClient.getWeather(lat, lon)
            if (result != null) {
                _uiState.update { 
                    it.copy(
                        latitude = result.latitude,
                        longitude = result.longitude,
                        temperature = result.current.temperature,
                        windspeed = result.current.windSpeed,
                        winddirection = result.current.windDirection,
                        weathercode = result.current.weatherCode,
                        seaLevelPressure = result.current.surfacePressure,
                        time = result.current.time,
                        isDay = result.current.isDay,
                        isLoading = false,
                        weatherData = result
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, errorResId = dam_A51388.coolweatherapp.R.string.error_fetching) }
            }
        }
    }
}
