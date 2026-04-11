package dam_A51388.coolweatherapp


data class WeatherData(
    val latitude: Double,
    val longitude: Double,
    val timezone: String?,
    val current: Current?,
    val daily: Daily?,
    val hourly: Hourly?
)

data class Current(
    val time: String?,
    val temperature_2m: Float,
    val relative_humidity_2m: Int,
    val apparent_temperature: Float,
    val is_day: Int,
    val weather_code: Int,
    val wind_speed_10m: Float,
    val wind_direction_10m: Int,
    val precipitation: Float,
    val cloud_cover: Int,
    val surface_pressure: Double,
    val wind_gusts_10m: Float
)

data class Daily(
    val time: ArrayList<String>?,
    val sunrise: ArrayList<String>?,
    val sunset: ArrayList<String>?,
    val precipitation_sum: ArrayList<Float>?
)

data class Hourly(
    val time: ArrayList<String>?,
    val uv_index: ArrayList<Float>?,
    val relative_humidity_2m: ArrayList<Int>?,
    val precipitation_probability: ArrayList<Int>?,
    val visibility: ArrayList<Int>?
)

data class WeatherCodeInfo(
    val code: Int,
    val description: String,
    val imagePrefix: String
)
