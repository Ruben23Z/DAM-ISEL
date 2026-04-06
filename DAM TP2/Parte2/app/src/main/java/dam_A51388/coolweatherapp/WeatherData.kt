package dam_A51388.coolweatherapp

// ─── Raiz do JSON ────────────────────────────────────────────────────────────
data class WeatherData(
    var latitude: Double,
    var longitude: Double,
    var timezone: String,
    var current: Current,
    var daily: Daily,
    var hourly: Hourly,
    var minutely_15: Minutely15
)

// ─── Bloco "current" ─────────────────────────────────────────────────────────
data class Current(
    var temperature_2m: Float,
    var apparent_temperature: Float,
    var is_day: Int,
    var weather_code: Int,
    var wind_speed_10m: Float,
    var wind_direction_10m: Int,
    var precipitation: Float,
    var rain: Float,
    var snowfall: Float,
    var cloud_cover: Int,
    var surface_pressure: Double
)

// ─── Bloco "daily" ───────────────────────────────────────────────────────────
data class Daily(
    var time: ArrayList<String>,
    var weather_code: ArrayList<Int>,
    var temperature_2m_max: ArrayList<Float>,
    var temperature_2m_min: ArrayList<Float>,
    var precipitation_probability_max: ArrayList<Int>,
    var precipitation_sum: ArrayList<Float>,
    var precipitation_hours: ArrayList<Int>,
    var uv_index_max: ArrayList<Float>,
    var sunrise: ArrayList<String>,
    var sunset: ArrayList<String>
)

// ─── Bloco "hourly" ──────────────────────────────────────────────────────────
data class Hourly(
    var time: ArrayList<String>,
    var temperature_2m: ArrayList<Float>,
    var relative_humidity_2m: ArrayList<Int>,
    var apparent_temperature: ArrayList<Float>,
    var precipitation_probability: ArrayList<Int>,
    var precipitation: ArrayList<Float>,
    var weather_code: ArrayList<Int>,
    var is_day: ArrayList<Int>,
    var uv_index: ArrayList<Float>,
    var wind_speed_10m: ArrayList<Float>,
    var wind_direction_10m: ArrayList<Int>
)

// ─── Bloco "minutely_15" ─────────────────────────────────────────────────────
data class Minutely15(
    var time: ArrayList<String>,
    var temperature_2m: ArrayList<Float>,
    var apparent_temperature: ArrayList<Float>,
    var relative_humidity_2m: ArrayList<Int>,
    var precipitation: ArrayList<Float>,
    var weather_code: ArrayList<Int>,
    var wind_speed_10m: ArrayList<Float>,
    var wind_direction_10m: ArrayList<Int>,
    var wind_gusts_10m: ArrayList<Float>,
    var visibility: ArrayList<Int>
)

// ─── Códigos WMO ─────────────────────────────────────────────────────────────
enum class WMO_WeatherCode(var code: Int, var image: String) {
    CLEAR_SKY(0, "clear_"),
    MAINLY_CLEAR(1, "mostly_clear_"),
    PARTLY_CLOUDY(2, "partly_cloudy_"),
    OVERCAST(3, "cloudy"),
    FOG(45, "fog"),
    DEPOSITING_RIME_FOG(48, "fog"),
    DRIZZLE_LIGHT(51, "drizzle"),
    DRIZZLE_MODERATE(53, "drizzle"),
    DRIZZLE_DENSE(55, "drizzle"),
    FREEZING_DRIZZLE_LIGHT(56, "freezing_drizzle"),
    FREEZING_DRIZZLE_DENSE(57, "freezing_drizzle"),
    RAIN_SLIGHT(61, "rain_light"),
    RAIN_MODERATE(63, "rain"),
    RAIN_HEAVY(65, "rain_heavy"),
    FREEZING_RAIN_LIGHT(66, "freezing_rain_light"),
    FREEZING_RAIN_HEAVY(67, "freezing_rain_heavy"),
    SNOW_FALL_SLIGHT(71, "snow_light"),
    SNOW_FALL_MODERATE(73, "snow"),
    SNOW_FALL_HEAVY(75, "snow_heavy"),
    SNOW_GRAINS(77, "snow"),
    RAIN_SHOWERS_SLIGHT(80, "rain_light"),
    RAIN_SHOWERS_MODERATE(81, "rain"),
    RAIN_SHOWERS_VIOLENT(82, "rain_heavy"),
    SNOW_SHOWERS_SLIGHT(85, "snow_light"),
    SNOW_SHOWERS_HEAVY(86, "snow_heavy"),
    THUNDERSTORM_SLIGHT_MODERATE(95, "tstorm"),
    THUNDERSTORM_HAIL_SLIGHT(96, "tstorm"),
    THUNDERSTORM_HAIL_HEAVY(99, "tstorm")
}

fun getWeatherCodeMap(): Map<Int, WMO_WeatherCode> {
    val weatherMap = HashMap<Int, WMO_WeatherCode>()
    WMO_WeatherCode.values().forEach {
        weatherMap[it.code] = it
    }
    return weatherMap
}
