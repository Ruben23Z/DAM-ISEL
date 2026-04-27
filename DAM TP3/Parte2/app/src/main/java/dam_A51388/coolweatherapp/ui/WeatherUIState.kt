package dam_A51388.coolweatherapp.ui

import dam_A51388.coolweatherapp.data.WeatherData

sealed interface WeatherUiState {
    object Loading : WeatherUiState
    data class Success(val data: WeatherData) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}
