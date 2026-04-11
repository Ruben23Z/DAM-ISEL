package damA51388.galeriaaleatoria.repository

import damA51388.galeriaaleatoria.model.ImageItem
import damA51388.galeriaaleatoria.network.DogApiService

/**
 * Repository for Dog images.
 */
class ImageRepository(
    private val api: DogApiService = DogApiService.instance
) {

    /**
     * Fetch random dog images and convert them to ImageItem objects.
     */
    suspend fun fetchRandomImages(count: Int = 10): List<ImageItem> {
        val response = api.getRandomDogImages(count)
        return if (response.status == "success") {
            response.urls.map { ImageItem.fromUrl(it) }
        } else {
            emptyList()
        }
    }
}
