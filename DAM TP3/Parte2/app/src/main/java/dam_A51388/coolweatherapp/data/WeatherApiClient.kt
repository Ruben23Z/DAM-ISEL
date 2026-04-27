package dam_A51388.coolweatherapp.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object WeatherApiClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun getWeather(lat: Float, lon: Float): WeatherData? {
        val reqString = buildString {
            append("https://api.open-meteo.com/v1/forecast?")
            append("latitude=${lat}&longitude=${lon}&")
            append("current=temperature_2m,weather_code,is_day,wind_speed_10m,wind_direction_10m,apparent_temperature,surface_pressure&")
            append("hourly=temperature_2m,weather_code,precipitation_probability,windspeed_10m,pressure_msl&")
            append("daily=sunrise,sunset,uv_index_max&")
            append("timezone=auto&forecast_days=1")
        }
        System.out.println (" Getting URL : $reqString ")
        return try {
            client.get(reqString).body()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
