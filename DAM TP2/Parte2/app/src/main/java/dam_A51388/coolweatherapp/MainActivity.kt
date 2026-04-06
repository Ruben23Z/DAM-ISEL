package dam_A51388.coolweatherapp

import android.content.res.Configuration
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private var day: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        day = if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) false else true

        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                if (day) {
                    setTheme(R.style.Theme_Day)
                } else {
                    setTheme(R.style.Theme_Night)
                }
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                if (day) {
                    setTheme(R.style.Theme_Day_Land)
                } else {
                    setTheme(R.style.Theme_Night_Land)
                }
            }
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup update button
        val updateButton: Button = findViewById(R.id.btn_update)
        updateButton.setOnClickListener {
            System.out.println("Update button clicked")
            fetchWeatherData(38.7223f, -9.1393f).start()
        }

        // Fetch initial weather data
        fetchWeatherData(-22.9035f,  -43.2096f).start()
    }

    private fun WeatherAPI_Call(lat: Float, long: Float): WeatherData {
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

     private fun fetchWeatherData(lat: Float, long: Float): Thread {
         return Thread {
             val weather = WeatherAPI_Call(lat, long)
             updateUI(weather)
         }
     }

     private fun updateUI(request: WeatherData) {
         runOnUiThread {
             val weatherImage: ImageView = findViewById(R.id.weatherImage)
             val pressureValue: TextView = findViewById(R.id.pressureValue)
             val humidadeValue: TextView = findViewById(R.id.humidadeValue)
             val latitudeValue: TextView = findViewById(R.id.latitudeValue)
             val altitudeValue: TextView = findViewById(R.id.altitudeValue)
             val velocidadeVentoValue: TextView = findViewById(R.id.velocidadeVentoValue)
             val direcaoVentoValue: TextView = findViewById(R.id.direcaoVentoValue)
             val tempoValue: TextView = findViewById(R.id.tempoValue)
             val uvIndexValue: TextView = findViewById(R.id.uvIndexValue)
             val tvTemp: TextView = findViewById(R.id.tv_temp)

             val now = Calendar.getInstance()
             val currentHour = now.get(Calendar.HOUR_OF_DAY)
             val safeIndex = if (currentHour < request.hourly.time.size) currentHour else 0

             tvTemp.text = request.current.temperature_2m.toInt().toString() + "°"
             pressureValue.text = request.current.surface_pressure.toString() + " hPa"
             humidadeValue.text = request.hourly.relative_humidity_2m[safeIndex].toString() + "%"

             uvIndexValue.text = request.hourly.uv_index[safeIndex].toString()

             latitudeValue.text = request.latitude.toString() + "°"
             altitudeValue.text = request.longitude.toString() + "°"
             velocidadeVentoValue.text = request.current.wind_speed_10m.toString() + " km/h"
             direcaoVentoValue.text = request.current.wind_direction_10m.toString() + "°"

             val minute = String.format("%02d", now.get(Calendar.MINUTE))
             tempoValue.text = request.timezone + ": " + currentHour + ":" + minute

             val mapt = getWeatherCodeMap()
             val wCode = mapt.get(request.current.weather_code)
             val wImage = when (wCode) {
                 WMO_WeatherCode.CLEAR_SKY,
                 WMO_WeatherCode.MAINLY_CLEAR,
                 WMO_WeatherCode.PARTLY_CLOUDY -> if (day) wCode?.image + "day" else
                     wCode?.image + "night"
                 else -> wCode?.image
             }

             val res = getResources()
             weatherImage.setImageResource(R.drawable.fog)
             val resID = res.getIdentifier(wImage, "drawable", getPackageName())
             val drawable = this.getDrawable(resID)
             weatherImage.setImageDrawable(drawable)
         }
     }

    fun onUpdateWeatherClick(v: android.view.View) {
        fetchWeatherData(38.7223f, -9.1393f).start()
    }
}
