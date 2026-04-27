package dam_A51388.coolweatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam_A51388.coolweatherapp.data.WeatherApiClient
import dam_A51388.coolweatherapp.ui.WeatherUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private var currentLat = 38.7169f
    private var currentLon = -9.1395f

    init {
        fetchWeather()
    }

    fun fetchWeather(lat: Float = currentLat, lon: Float = currentLon) {
        currentLat = lat
        currentLon = lon
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            val result = WeatherApiClient.getWeather(lat, lon)
            if (result != null) {
                _uiState.value = WeatherUiState.Success(result)
            } else {
                _uiState.value = WeatherUiState.Error("Erro ao obter dados meteorológicos")
            }
        }
    }
}
