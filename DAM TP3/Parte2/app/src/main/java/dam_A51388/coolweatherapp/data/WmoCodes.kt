package dam_A51388.coolweatherapp.data

enum class WMO_WeatherCode(val code: Int, val description: String, val image: String) {
    CLEAR_SKY(0, "Clear Sky", "sunny"), MAINLY_CLEAR(1, "Mainly Clear", "sunny"), PARTLY_CLOUDY(
        2, "Partly Cloudy", "cloudy"
    ),
    OVERCAST(3, "Overcast", "overcast"), FOG(45, "Fog", "fog"), DEPOSITE_RIME_FOG(
        48, "Fog", "fog"
    ),
    DRIZZLE_LIGHT(51, "Light Drizzle", "drizzle"), DRIZZLE_MODERATE(
        53, "Moderate Drizzle", "drizzle"
    ),
    DRIZZLE_DENSE(55, "Dense Drizzle", "drizzle"), RAIN_SLIGHT(
        61, "Slight Rain", "rain"
    ),
    RAIN_MODERATE(63, "Moderate Rain", "rain"), RAIN_HEAVY(
        65, "Heavy Rain", "rain"
    ),
    SNOW_SLIGHT(71, "Slight Snow", "snow"), SNOW_MODERATE(73, "Moderate Snow", "snow"), SNOW_HEAVY(
        75, "Heavy Snow", "snow"
    ),
    THUNDERSTORM(95, "Thunderstorm", "storm"), THUNDERSTORM_HAIL_SLIGHT(
        96, "Thunderstorm with Hail", "storm"
    ),
    THUNDERSTORM_HAIL_HEAVY(99, "Thunderstorm with Heavy Hail", "storm");

    companion object {
        fun fromCode(code: Int): WMO_WeatherCode? = values().find { it.code == code }
    }
}

fun getWeatherCodeMap(): Map<Int, WMO_WeatherCode> {
    return WMO_WeatherCode.values().associateBy { it.code }
}
