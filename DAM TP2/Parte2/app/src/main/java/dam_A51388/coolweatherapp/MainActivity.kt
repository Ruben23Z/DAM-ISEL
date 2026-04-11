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

/**
 * MainActivity: Controlador principal do ciclo de vida da aplicação. Esta classe é responsável pela
 * coordenação entre a interface de utilizador, os serviços de localização do sistema e a lógica de
 * apresentação (ViewModel).
 */
class MainActivity : AppCompatActivity() {

  // Constante de controlo para o protocolo de requisição de permissões
  private val LOCATION_REQUEST_CODE = 100
  // Objeto de vinculação (Binding) para acesso tipado aos componentes do layout
  private lateinit var binding: ActivityMainBinding
  // Fornecedor de serviços de localização da Google Play Services
  private lateinit var fusedLocationClient: FusedLocationProviderClient
  // Referência para o ViewModel (Padrão de Desenho MVVM)
  private lateinit var viewModel: WeatherViewModel

  /** Propriedades estáticas para persistência de estado simples entre recriações de atividades. */
  companion object {
    var day: Boolean = true // Estado binário para controlo de iluminação (Dia/Noite)
  }

  /**
   * Implementação da lógica de obtenção da localização geográfica do dispositivo. Utiliza o
   * FusedLocationProviderClient para uma abstração eficiente dos sensores de hardware.
   */
  fun getLocation() {
    // Validação preventiva dos privilégios de acesso aos dados de geolocalização
    if (
      ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED
    ) {
      // Requisição assíncrona da última coordenada geográfica registada
      fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
          // Conversão de tipos primitivos e invocação da lógica de negócio no ViewModel
          val lat = location.latitude.toFloat()
          val long = location.longitude.toFloat()
          viewModel.fetchWeather(lat, long)
        }
      }
    } else {
      // Protocolo de consulta ao utilizador para atribuição de permissões
      ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        LOCATION_REQUEST_CODE,
      )
    }
  }

  /**
   * Orquestração do processo de inicialização da Atividade. Define o tema condicional, inicializa o
   * Data Binding e configura os observáveis.
   */
  override fun onCreate(savedInstanceState: Bundle?) {
    // Seleção dinâmica do tema baseada na orientação do dispositivo e estado lumínico
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

    // Inflação do layout e inicialização do objeto de vinculação (Data Binding)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

    // Instanciação e associação do ViewModel ao ciclo de vida da atividade
    viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
    binding.viewModel = viewModel

    // Atribuição do LifecycleOwner para permitir a reatividade automática dos LiveData
    binding.lifecycleOwner = this

    // Inicialização do cliente de geolocalização e desencadeamento do fluxo de dados
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    getLocation()

    /**
     * Implementação do Padrão Observer para monitorização do estado meteorológico. Sempre que
     * ocorre uma mutação nos dados, verifica-se a conformidade do tema visual.
     */
    viewModel.liveWeatherData.observe(this) { weather ->
      weather?.current?.let { current ->
        // Mapeamento do valor discreto (int) para representação lógica (boolean)
        val isDayNow = current.is_day >= 1

        // Avaliação da necessidade de reestruturação da interface (Recreate)
        if (isDayNow != day) {
          day = isDayNow
          recreate()
        }
      }
    }
  }

  /**
   * Método de 'callback' para o evento de clique no botão de atualização (Data Binding). Reinicia o
   * ciclo de obtenção de dados geográficos e meteorológicos.
   */
  fun onUpdateWeatherClick(v: android.view.View) {
    getLocation()
  }
}
