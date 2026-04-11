package dam_A51388.coolweatherapp


import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL

/**
 * Classe de Repositório responsável pela mediação de dados entre a fonte externa (API) e a aplicação.
 * Implementa o padrão Repository para abstrair a lógica de obtenção de dados climáticos.
 */
class WeatherRepo {

    /**
     * Efetua a requisição de informações meteorológicas a partir de coordenadas geográficas.
     * @param lat Latitude da localização pretendida.
     * @param long Longitude da localização pretendida.
     * @return Objeto WeatherData com as informações obtidas ou null em caso de falha.
     */
    fun fetchWeatherFromApi(lat: Float, long: Float): WeatherData? {
        // Envolve a operação num bloco de tratamento de exceções para garantir a robustez do sistema
        return try {
            val reqString = buildString {
                append("https://api.open-meteo.com/v1/forecast?")
                append("latitude=${lat}&longitude=${long}&")
                append(
                    "current=relative_humidity_2m,temperature_2m,weather_code,is_day,wind_speed_10m,wind_direction_10m,surface_pressure,apparent_temperature,precipitation,cloud_cover,wind_gusts_10m&"
                )
                append(
                    "hourly=uv_index,relative_humidity_2m,precipitation_probability,visibility&"
                )
                append(
                    "daily=sunset,sunrise,precipitation_sum&"
                )
                append("timezone=auto")
            }

            // Instancia o objeto URL com a string de requisição previamente consolidada
            val url = URL(reqString)

            // Abre o fluxo de entrada (stream) e assegura o encerramento automático do recurso após o uso
            url.openStream().use { fluxo ->
                // Deserializa o JSON obtido para a estrutura de dados WeatherData utilizando a biblioteca GSON
                val request = Gson().fromJson(InputStreamReader(fluxo, "UTF-8"), WeatherData::class.java)
                // Retorna a instância preenchida com os dados da API
                request
            }
        } catch (e: Exception) {
            // Emite uma mensagem de erro na consola para diagnóstico em fase de desenvolvimento
            System.out.println("Ocorreu uma falha na comunicação com a API: ${e.message}")
            // Retorna uma referência nula para indicar a impossibilidade de obter os dados
            null
        }
    }
}
