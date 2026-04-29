package damA51388.galeriaaleatoria.network

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * API response for Dog CEO API.
 */
data class DogApiResponse(
    @SerializedName("message") val urls: List<String>,
    val status: String
)

/**
 * Dog CEO API service.
 * Base URL: https://dog.ceo/api/
 */
interface DogApiService {

    @GET("breeds/image/random/{count}")
    suspend fun getRandomDogImages(
        @Path("count") count: Int
    ): DogApiResponse

    companion object {
        private const val BASE_URL = "https://dog.ceo/api/"

        val instance: DogApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DogApiService::class.java)
        }
    }
}
