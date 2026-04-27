package dam_A51388.coolweatherapp.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherData(
    val latitude: Float,
    val longitude: Float,
    val timezone: String,
    @SerialName("utc_offset_seconds") val utcOffset: Int,
    val current: CurrentWeather,
    val hourly: HourlyWeather,
    val daily: DailyWeather
)

@Serializable
data class CurrentWeather(
    val time: String,
    @SerialName("temperature_2m") val temperature: Float,
    @SerialName("apparent_temperature") val apparentTemperature: Float,
    @SerialName("weather_code") val weatherCode: Int,
    @SerialName("is_day") val isDay: Int,
    @SerialName("wind_speed_10m") val windSpeed: Float,
    @SerialName("wind_direction_10m") val windDirection: Int,
    @SerialName("surface_pressure") val surfacePressure: Float
)

@Serializable
data class HourlyWeather(
    val time: List<String>,
    @SerialName("temperature_2m") val temperatures: List<Float>,
    @SerialName("weather_code") val weatherCodes: List<Int>,
    @SerialName("precipitation_probability") val precipitationProbability: List<Int>,
    @SerialName("windspeed_10m") val windSpeeds: List<Float>,
    @SerialName("pressure_msl") val pressures: List<Float>
)

@Serializable
data class DailyWeather(
    val time: List<String>,
    val sunrise: List<String>,
    val sunset: List<String>,
    @SerialName("uv_index_max") val uvIndexMax: List<Float>
)

val wmoLabels = mapOf(
    0 to "Céu limpo",
    1 to "Maioritariamente limpo",
    2 to "Parcialmente nublado",
    3 to "Nublado",
    45 to "Nevoeiro",
    48 to "Nevoeiro gelado",
    51 to "Chuvisco leve",
    53 to "Chuvisco",
    55 to "Chuvisco intenso",
    61 to "Chuva leve",
    63 to "Chuva moderada",
    65 to "Chuva intensa",
    71 to "Neve leve",
    73 to "Neve",
    75 to "Neve intensa",
    80 to "Aguaceiros",
    81 to "Aguaceiros moderados",
    82 to "Aguaceiros intensos",
    95 to "Trovoada",
    96 to "Trovoada c/ granizo",
    99 to "Trovoada intensa"
)

fun getWeatherIconName(code: Int, isDay: Int): String {
    return when (code) {
        0 -> if (isDay == 1) "clear_day" else "clear_night"
        1 -> if (isDay == 1) "mostly_clear_day" else "mostly_clear_night"
        2 -> if (isDay == 1) "partly_cloudy_day" else "partly_cloudy_night"
        3 -> "mostly_cloudy"
        45, 48 -> "fog"
        51, 53, 55 -> "drizzle"
        61 -> "rain_light"
        63 -> "rain"
        65 -> "rain_heavy"
        71 -> "snow_light"
        73 -> "snow"
        75 -> "snow_heavy"
        80, 81, 82 -> "rain"
        95, 96, 99 -> "tstorm"
        else -> "mostly_cloudy"
    }
}
