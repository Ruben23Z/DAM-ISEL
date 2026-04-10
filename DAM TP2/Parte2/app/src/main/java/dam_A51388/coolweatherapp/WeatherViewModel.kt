package dam_A51388.coolweatherapp

import android.app.Application
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL

/**
 * WeatherViewModel: Componente da camada de Apresentação fundamentado no padrão MVVM.
 * Atua como intermediário entre o repositório de dados e a Interface de Utilizador (UI),
 * garantindo a sobrevivência dos dados perante alterações de configuração.
 */
class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    // Instância do repositório para centralizar a obtenção de dados externos
    private val repository = WeatherRepo()

    // Encapsula os dados climatéricos brutos; acessível apenas internamente
    private val weatherData = MutableLiveData<WeatherData>()
    // Disponibiliza os dados climáticos para observação externa de forma imutável
    val LiveWeatherData: LiveData<WeatherData> get() = weatherData

    // LiveData que armazena a referência para o recurso gráfico (ícone) do estado do tempo
    val weatherIconRes = MutableLiveData<Int>()
    // LiveData que contém a descrição textual legível do estado climatérico atual
    val statusDescription = MutableLiveData<String>()

    // Mapa estruturado que associa códigos numéricos WMO a informações descritivas e visuais
    private var weatherMap: Map<Int, WeatherCodeInfo> = loadWeatherCodes()

    /**
     * Inicia o processo assíncrono de obtenção de dados meteorológicos.
     * @param lat Coordenada de latitude.
     * @param long Coordenada de longitude.
     */
    fun fetchWeather(lat: Float, long: Float) {
        // Executa a operação de rede numa linha de execução (Thread) secundária para não bloquear a UI
        Thread {
            // Solicita os dados ao repositório de forma síncrona dentro da Thread
            val weather = repository.fetchWeatherFromApi(lat, long)
            
            // Verifica a integridade dos dados recebidos antes de proceder à atualização
            if (weather != null) {
                // Notifica os observadores sobre a nova instância de dados obtida
                weatherData.postValue(weather)
                // Desencadeia o processamento lógico para extrair ícones e descrições
                processWeatherData(weather)
            }
        }.start()
    }

    /**
     * Carrega e estrutura os códigos de meteorologia a partir dos recursos do sistema (strings.xml/arrays.xml).
     * @return Um mapa mapeando o código WMO ao seu objeto de informação correspondente.
     */
    private fun loadWeatherCodes(): Map<Int, WeatherCodeInfo> {
        val context = getApplication<Application>()
        // Obtém arrays de recursos definidos no ficheiro strings.xml (ou equivalente)
        val codes = context.resources.getIntArray(R.array.weather_codes)
        val images = context.resources.getStringArray(R.array.weather_images)
        val descriptions = context.resources.getStringArray(R.array.weather_descriptions)

        val map = mutableMapOf<Int, WeatherCodeInfo>()
        // Itera sobre as coleções para consolidar as informações num mapa associativo
        for (i in codes.indices) {
            val code = codes[i]
            val image = images[i]
            val description = descriptions[i]
            map[code] = WeatherCodeInfo(code, description, image)
        }
        return map
    }

    /**
     * Analisa os dados climáticos recebidos e atualiza os estados reativos (LiveData).
     * @param request Instância de WeatherData contendo as informações atualizadas.
     */
    private fun processWeatherData(request: WeatherData) {

        // Determina se o período atual é diurno com base na métrica is_day da API
        val day = request.current.is_day >= 1

        // Recupera as meta-informações associadas ao código de tempo atual
        val weatherInfo = weatherMap[request.current.weather_code]
        
        // Constrói o nome do recurso gráfico, alternando entre prefixos de dia e noite quando aplicável (códigos 0-2)
        val imageName = if (weatherInfo?.code == 0 || weatherInfo?.code == 1 || weatherInfo?.code == 2) {
            if (day) weatherInfo?.imagePrefix + "day" else weatherInfo?.imagePrefix + "night"
        } else {
            weatherInfo?.imagePrefix
        }

        // Atualiza a descrição textual do estado do tempo
        statusDescription.postValue(weatherInfo?.description ?: "Desconhecido")
        
        // Resolve o identificador numérico do recurso drawable a partir do seu nome textual
        val resID = if (imageName != null) {
            getApplication<Application>().resources.getIdentifier(
                imageName, "drawable", getApplication<Application>().packageName
            )
        } else 0

        // Atualiza o ícone visual; caso o recurso não seja encontrado, define uma imagem por omissão (fog)
        weatherIconRes.postValue(if (resID != 0) resID else R.drawable.fog)
    }
}

/**
 * Binding Adapter customizado para permitir a atribuição de recursos drawable diretamente no XML.
 * @param imageView Elemento de visualização de imagem.
 * @param resource Identificador numérico do recurso a ser exibido.
 */
@BindingAdapter("imageResource")
fun setImageResource(imageView: ImageView, resource: Int) {
    // Define a imagem do componente a partir do ID do recurso fornecido
    imageView.setImageResource(resource)
}
