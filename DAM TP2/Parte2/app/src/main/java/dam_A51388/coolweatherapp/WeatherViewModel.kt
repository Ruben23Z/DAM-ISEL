package dam_A51388.coolweatherapp

import android.app.Application
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * WeatherViewModel: Componente da camada de Apresentação fundamentado no padrão MVVM.
 * Atua como mediador entre a fonte de dados (WeatherRepo) e a Interface de Utilizador (UI).
 * Gere o estado reativo da aplicação através de instâncias de LiveData.
 */
class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    // Instanciação do repositório para acesso a dados meteorológicos externos
    private val repository = WeatherRepo()

    // Encapsulamento de dados reativos (Encapsulation Printiple)
    private val weatherData = MutableLiveData<WeatherData>()
    val liveWeatherData: LiveData<WeatherData> get() = weatherData

    // Observáveis para atualização dinâmica da Interface de Utilizador
    val weatherIconRes = MutableLiveData<Int>()
    val statusDescription = MutableLiveData<String>()

    // Métricas formatadas para exibição direta na UI via Data Binding
    val currentTime = MutableLiveData<String>()
    val currentUV = MutableLiveData<String>()
    val probRain = MutableLiveData<String>()
    val visibility = MutableLiveData<String>()
    val sunrise = MutableLiveData<String>()
    val sunset = MutableLiveData<String>()
    val apparentTemp = MutableLiveData<String>()
    val cloudCover = MutableLiveData<String>()
    val windGusts = MutableLiveData<String>()
    val precipSum = MutableLiveData<String>()

    // Mapa para tradução de códigos meteorológicos WMO
    private var weatherMap: Map<Int, WeatherCodeInfo> = loadWeatherCodes()

    /**
     * Inicia o fluxo de obtenção de dados meteorológicos numa linha de execução secundária (Worker Thread).
     *
     * @param lat Coordenada de latitude.
     * @param long Coordenada de longitude.
     */
    fun fetchWeather(lat: Float, long: Float) {
        Thread {
            val weather = repository.fetchWeatherFromApi(lat, long)
            if (weather != null) {
                // Notificação dos observadores na linha de execução principal (Main Thread)
                weatherData.postValue(weather)
                processWeatherdata(weather)
            }
        }.start()
    }

    /**
     * Método auxiliar para a extração do índice horário a partir de uma carimbo temporal ISO-8601.
     *
     * @param isoTime Representação textual do tempo.
     * @return Índice inteiro correspondente à hora (0-23).
     */
    private fun extracaoHorasIndex(isoTime: String): Int {

        try {
            // Exemplo esperado: "2025-03-25T14:30"

            // 1. Separar data e hora pelo 'T'
            val partes = isoTime.split("T")

            // Verificação de segurança
            if (partes.size < 2) return 0

            // 2. Pegar a parte da hora → "14:30"
            val parteHora = partes[1]

            // 3. Separar hora e minutos pelo ':'
            val horaMinutos = parteHora.split(":")

            // Verificação de segurança
            if (horaMinutos.isEmpty()) return 0

            // 4. Pegar apenas a hora → "14"
            val horaString = horaMinutos[0]

            // 5. Converter para inteiro
            val horaInt = horaString.toInt()

            return horaInt

        } catch (e: Exception) {
            // Em caso de erro (formato inválido)
            return 0
        }
    }

    /**
     * Inicializa o mapa de mapeamento meteorológico a partir dos recursos do sistema (strings.xml / arrays.xml).
     */
    private fun loadWeatherCodes(): Map<Int, WeatherCodeInfo> {
        val context = getApplication<Application>()
        val codes = context.resources.getIntArray(R.array.weather_codes)
        val images = context.resources.getStringArray(R.array.weather_images)
        val descriptions = context.resources.getStringArray(R.array.weather_descriptions)

        val map = mutableMapOf<Int, WeatherCodeInfo>()
        for (i in codes.indices) {
            val code = codes[i]
            val image = images[i]
            val description = descriptions[i]
            map[code] = WeatherCodeInfo(code, description, image)
        }
        return map
    }

    /**
     * Implementação da lógica de tratamento e formatação de dados meteorológicos.
     * Este método transforma dados brutos da API em representações legíveis para o utilizador final.
     */
    private fun processWeatherdata(request: WeatherData) {
        // Validação da integridade dos blocos de dados recebidos
        val current = request.current
        val hourly = request.hourly
        val daily = request.daily


        if (current == null || hourly == null || daily == null) {
            return
        }

        // 1. Processamento e formatação da temporização local do sistema
        val fullTime = current.time ?: ""
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val TempoValor = sdf.format(Date())
        currentTime.postValue(TempoValor)

        // 2. Determinação da posição relativa no fluxo horário
        val horaIndx = extracaoHorasIndex(fullTime)

        // 3. Atualização das métricas com resolução horária (Índice UV, Precipitação e Visibilidade)
        val uvList = hourly.uv_index
        if (uvList != null && horaIndx < uvList.size) {
            currentUV.postValue(uvList[horaIndx].toString())
        }

        val probList = hourly.precipitation_probability
        if (probList != null && horaIndx < probList.size) {
            probRain.postValue(probList[horaIndx].toString() + "%")
        }

        val visList = hourly.visibility
        if (visList != null && horaIndx < visList.size) {
            val km = visList[horaIndx] / 1000.0 // Conversão dimensional de escalas (M -> KM)
            visibility.postValue(km.toString() + " km")
        }

        // 4. Processamento de indicadores solares diários
        val sunriseList = daily.sunrise
        if (sunriseList != null && sunriseList.size > 0) {
            val rawTime = sunriseList[0].split("T")[1]
            val sunTime = if (rawTime.length >= 5) rawTime.substring(0, 5) else rawTime
            sunrise.postValue(sunTime)
        }

        val sunsetList = daily.sunset
        if (sunsetList != null && sunsetList.size > 0) {
            val rawTime = sunsetList[0].split("T")[1]
            val setTime = if (rawTime.length >= 5) rawTime.substring(0, 5) else rawTime
            sunset.postValue(setTime)
        }

        val precipList = daily.precipitation_sum
        if (precipList != null && precipList.size > 0) {
            precipSum.postValue(precipList[0].toString() + " mm")
        }

        // 5. Instanciação de valores para métricas de conforto térmico e condições atmosféricas
        apparentTemp.postValue(current.apparent_temperature.toString() + "º")
        cloudCover.postValue(current.cloud_cover.toString() + "%")
        windGusts.postValue(current.wind_gusts_10m.toString() + " km/h")

        val code = current.weather_code
        val day = current.is_day >= 1
        val info = weatherMap[code]

        if (info == null) {
            statusDescription.postValue("Unknown")
            weatherIconRes.postValue(R.drawable.fog)
        } else {

            statusDescription.postValue(info.description)

            var name = info.imagePrefix
            // Lógica condicional para diferenciação luminosa (Ciclo Dia/Noite)
            if (code == 0 || code == 1 || code == 2) {
                if (day) {
                    name = name + "day"
                } else {
                    name = name + "night"
                }
            }

            // Invocação dinâmica do identificador do recurso gráfico via sistema de reflexão
            val resID = getApplication<Application>().resources.getIdentifier(
                name, "drawable", getApplication<Application>().packageName
            )

            if (resID != 0) {
                weatherIconRes.postValue(resID)
            } else {
                weatherIconRes.postValue(R.drawable.fog) // Recurso de salvaguarda (Fallback)
            }
        }
    }
}

/**
 * BindingAdapter: Utilitário para a orquestração de propriedades costumizadas no paradigma Data Binding.
 */
@BindingAdapter("imageResource")
fun setImageResource(imageView: ImageView, resource: Int) {
    imageView.setImageResource(resource)
}
