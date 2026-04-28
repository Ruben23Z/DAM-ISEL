package dam_A51388.coolweatherapp.data




//Este enum mapeia os códigos WMO (World Meteorological Organization).
//Transforma um número (ex: 0, 2) num texto descritivo (ex: "Céu limpo")
//e num nome de imagem (ex: "clear_day").
//É usado para converter o código numérico da API para algo legível pelo utilizador.

enum class WMO_WeatherCode(val code: Int, val description: String, val image: String) {
    CLEAR_SKY(0, "Céu limpo", "clear_"),
    MAINLY_CLEAR(1, "Maioritariamente limpo", "mostly_clear_"),
    PARTLY_CLOUDY(2, "Parcialmente nublado", "partly_cloudy_"),
    OVERCAST(3, "Nublado", "mostly_cloudy"),
    FOG(45, "Nevoeiro", "fog"),
    RIME_FOG(48, "Nevoeiro gelado", "fog"),
    DRIZZLE_LIGHT(51, "Chuvisco leve", "drizzle"),
    DRIZZLE_MODERATE(53, "Chuvisco", "drizzle"),
    DRIZZLE_DENSE(55, "Chuvisco intenso", "drizzle"),
    RAIN_SLIGHT(61, "Chuva leve", "rain_light"),
    RAIN_MODERATE(63, "Chuva moderada", "rain"),
    RAIN_HEAVY(65, "Chuva intensa", "rain_heavy"),
    SNOW_FALL_SLIGHT(71, "Neve leve", "snow_light"),
    SNOW_FALL_MODERATE(73, "Neve", "snow"),
    SNOW_FALL_HEAVY(75, "Neve intensa", "snow_heavy"),
    RAIN_SHOWERS_SLIGHT(80, "Aguaceiros", "rain"),
    RAIN_SHOWERS_MODERATE(81, "Aguaceiros moderados", "rain"),
    RAIN_SHOWERS_VIOLENT(82, "Aguaceiros intensos", "rain"),
    THUNDERSTORM_SLIGHT(95, "Trovoada", "tstorm"),
    THUNDERSTORM_HAIL_SLIGHT(96, "Trovoada c/ granizo", "tstorm"),
    THUNDERSTORM_HAIL_HEAVY(99, "Trovoada intensa", "tstorm");

    companion object {
        fun fromCode(code: Int): WMO_WeatherCode? {
            return values().find { it.code == code }
        }
    }
}

fun getWeatherCodeMap(): Map<Int, WMO_WeatherCode> {
    return WMO_WeatherCode.values().associateBy { it.code }
}
