package dam_A51388.coolweatherapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL

class WeatherViewModel: ViewModel() {

    private val weatherData = MutableLiveData<WeatherData>()
    val LiveWeatherData: LiveData<WeatherData> get() = weatherData

    fun fetchWeather(lat: Float, long: Float) {
        Thread {
            val weather = WeatherAPI_Call(lat, long)
            weatherData.postValue(weather)
        }.start()
    }

    fun WeatherAPI_Call(lat: Float, long: Float): WeatherData {
        val reqString = buildString {
            append("https://api.open-meteo.com/v1/forecast?")
            append("latitude=${lat}&longitude=${long}&")
            append("daily=uv_index_max,temperature_2m_max,temperature_2m_min,precipitation_probability_max,weather_code,sunset,sunrise,precipitation_sum,precipitation_hours&")
            append("hourly=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation_probability,precipitation,uv_index,is_day,weather_code,wind_speed_10m,wind_direction_10m&")
            append("current=is_day,rain,snowfall,temperature_2m,apparent_temperature,wind_speed_10m,wind_direction_10m,precipitation,cloud_cover,weather_code,surface_pressure&")
            append("minutely_15=precipitation,apparent_temperature,relative_humidity_2m,temperature_2m,wind_speed_10m,wind_direction_10m,wind_gusts_10m,visibility")
        }
        val url = URL(reqString)
        url.openStream().use {
            val request = Gson().fromJson(InputStreamReader(it, "UTF-8"), WeatherData::class.java)
            return request
        }
    }

}