package dam_A51388.coolweatherapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dam_A51388.coolweatherapp.databinding.ActivityMainBinding
import dam_A51388.coolweatherapp.ui.HourlyForecast
import dam_A51388.coolweatherapp.ui.HourlyForecastAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Ativar layout de ecrã inteiro
        enableEdgeToEdge()
        
        // Configurar ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Configuração inicial da UI estática (Lisboa - Sunny)
        setupStaticUI()
        setupHourlyForecast()
    }

    private fun setupStaticUI() {
        binding.tvLocation.text = "Lisboa"
        binding.tvDate.text = "TERÇA-FEIRA, 31 DE MARÇO"
        binding.tvTemp.text = "30°"
        binding.tvStatus.text = "SUNNY & CLEAR"
    }

    private fun setupHourlyForecast() {
        // Dados dummy para a previsão horária (Expert Static Demo)
        val forecastList = listOf(
            HourlyForecast("NOW", android.R.drawable.ic_menu_day, "30°"),
            HourlyForecast("15:00", android.R.drawable.ic_menu_day, "29°"),
            HourlyForecast("16:00", android.R.drawable.ic_menu_day, "28°"),
            HourlyForecast("17:00", android.R.drawable.ic_menu_day, "27°"),
            HourlyForecast("18:00", android.R.drawable.ic_menu_day, "25°"),
            HourlyForecast("19:00", android.R.drawable.ic_menu_day, "23°")
        )

        binding.rvHourlyForecast.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = HourlyForecastAdapter(forecastList)
        }
    }
}