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
            // Constrói programaticamente o endereço URL com os parâmetros de consulta necessários
            val reqString = buildString {
                append("https://api.open-meteo.com/v1/forecast?")
                append("latitude=${lat}&longitude=${long}&")
                // Solicitação de métricas diárias (índice UV, temperaturas extremas, etc.)
                append(
                    "daily=uv_index_max,temperature_2m_max,temperature_2m_min,precipitation_probability_max,weather_code,sunset,sunrise,precipitation_sum,precipitation_hours&"
                )
                // Solicitação de métricas horárias para detalhamento da previsão
                append(
                    "hourly=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation_probability,precipitation,uv_index,is_day,weather_code,wind_speed_10m,wind_direction_10m&"
                )
                // Solicitação de condições climatéricas atuais e pressão atmosférica
                append(
                    "current=is_day,rain,snowfall,temperature_2m,apparent_temperature,wind_speed_10m,wind_direction_10m,precipitation,cloud_cover,weather_code,surface_pressure&"
                )
                // Solicitação de dados de alta precisão (intervalos de 15 minutos)
                append(
                    "minutely_15=precipitation,apparent_temperature,relative_humidity_2m,temperature_2m,wind_speed_10m,wind_direction_10m,wind_gusts_10m,visibility"
                )
            }

            // Instancia o objeto URL com a string de requisição previamente consolidada
            val url = URL(reqString)
            
            // Abre o fluxo de entrada (stream) e assegura o encerramento automático do recurso após o uso
            url.openStream().use { fluxo ->
                // Deserializa o JSON obtido para a estrutura de dados WeatherData utilizando a biblioteca GSON
                val dadosObtidos = Gson().fromJson(InputStreamReader(fluxo, "UTF-8"), WeatherData::class.java)
                // Retorna a instância preenchida com os dados da API
                dadosObtidos
            }
        } catch (e: Exception) {
            // Emite uma mensagem de erro na consola para diagnóstico em fase de desenvolvimento
            System.out.println("Ocorreu uma falha na comunicação com a API: ${e.message}")
            // Retorna uma referência nula para indicar a impossibilidade de obter os dados
            null
        }
    }
}
