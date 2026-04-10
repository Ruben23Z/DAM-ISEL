package dam_A51388.coolweatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dam_A51388.coolweatherapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

  companion object { // para não reeniciar o seu valor quando a activity é recriada(recreate)
    var day: Boolean = true
  }

  private lateinit var fusedLocationClient: FusedLocationProviderClient
  private val LOCATION_REQUEST_CODE = 100
  private lateinit var binding: ActivityMainBinding
  private lateinit var viewModel: WeatherViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    // Definir o tema ANTES de qualquer outra coisa
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
    // setContentView(R.layout.activity_main)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

    // Inicialização do ViewModel
    viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

    binding.viewModel = viewModel
    binding.lifecycleOwner = this

    // Observador reativo do tempo (Ponto 4)
    viewModel.LiveWeatherData.observe(this) { weather ->
      val isDayNow = weather.current.is_day >= 1
      if (isDayNow != day) {
        day = isDayNow
        recreate() // Agora o valor persiste graças ao companion object!
      }
    }

    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    getLocation()
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray,
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == LOCATION_REQUEST_CODE) {
      if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        getLocation()
      }
    }
  }

  private fun getLocation() {
    if (
      ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED
    ) {
      fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
          val lat = location.latitude.toFloat()
          val long = location.longitude.toFloat()
          viewModel.fetchWeather(lat, long)
        }
      }
    } else {
      ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        LOCATION_REQUEST_CODE,
      )
    }
  }

  fun onUpdateWeatherClick(v: android.view.View) {
    System.out.println("Update button clicked")
    //        fetchWeatherData(-22.9035f, -43.2096f).start()
    getLocation()
  }

  // private fun setDay(request: WeatherData) {
  //     if (request.current.is_day >= 1) {
  //         day = true
  //     } else {
  //         day = false
  //     }
  // }
  //     private fun updateUI(request: WeatherData) {
  //         runOnUiThread {

  //             setDay(request)
  //             val weatherImage: ImageView = findViewById(R.id.weatherImage)
  //             val pressureValue: TextView = findViewById(R.id.pressureValue)
  //             val humidadeValue: TextView = findViewById(R.id.humidadeValue)
  //             val latitudeValue: TextView = findViewById(R.id.latitudeValue)
  //             val altitudeValue: TextView = findViewById(R.id.altitudeValue)
  //             val velocidadeVentoValue: TextView = findViewById(R.id.velocidadeVentoValue)
  //             val direcaoVentoValue: TextView = findViewById(R.id.direcaoVentoValue)
  //             val tempoValue: TextView = findViewById(R.id.tempoValue)
  //             val uvIndexValue: TextView = findViewById(R.id.uvIndexValue)
  //             val info_Temp: TextView = findViewById(R.id.info_temp)
  //             val info_Status: TextView = findViewById(R.id.info_status)

  //             val now = Calendar.getInstance()
  //             val currentHour = now.get(Calendar.HOUR_OF_DAY)
  //             val safeIndex = if (currentHour < request.hourly.time.size) currentHour else 0

  //             info_Temp.text = request.current.temperature_2m.toInt().toString() + "°"
  //             pressureValue.text = request.current.surface_pressure.toString() + " hPa"
  //             humidadeValue.text = request.hourly.relative_humidity_2m[safeIndex].toString() +
  // "%"
  //             uvIndexValue.text = request.hourly.uv_index[safeIndex].toString()
  //             latitudeValue.text = request.latitude.toString() + "°"
  //             altitudeValue.text = request.longitude.toString() + "°"
  //             velocidadeVentoValue.text = request.current.wind_speed_10m.toString() + " km/h"
  //             direcaoVentoValue.text = request.current.wind_direction_10m.toString() + "°"

  //             val minute = String.format("%02d", now.get(Calendar.MINUTE))
  //             tempoValue.text = request.timezone + ": " + currentHour + ":" + minute
  // //
  // //            val mapt = getWeatherCodeMap()
  // //            val wCode = mapt.get(request.current.weather_code)
  // //            val wImage = when (wCode) {
  // //                WMO_WeatherCode.CLEAR_SKY, WMO_WeatherCode.MAINLY_CLEAR,
  // WMO_WeatherCode.PARTLY_CLOUDY -> if (day) wCode?.image + "day" else wCode?.image + "night"
  // //
  // //                else -> wCode?.image
  // //            }

  //         }
  //     }

}
