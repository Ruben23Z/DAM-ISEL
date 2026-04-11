package damA51388.galeriaaleatoria.repository

import android.content.Context
import damA51388.galeriaaleatoria.model.ImageItem
import damA51388.galeriaaleatoria.network.DogApiService
import damA51388.galeriaaleatoria.storage.ImageCache

/**
 * Extension 6 — Repository with Offline Support
 */
class ImageRepository(
    context: Context,
    private val api: DogApiService = DogApiService.instance
) {
    private val cache = ImageCache(context)

    /**
     * Fetch random dog images and update cache.
     */
    suspend fun fetchRandomImages(count: Int = 10): List<ImageItem> {
        return try {
            val response = api.getRandomDogImages(count)
            if (response.status == "success") {
                val items = response.urls.map { ImageItem.fromUrl(it) }
                cache.addItems(items)
                items
            } else {
                getCachedImages()
            }
        } catch (e: Exception) {
            getCachedImages()
        }
    }

    /**
     * Returns items from local storage.
     */
    fun getCachedImages(): List<ImageItem> {
        return cache.getCachedItems()
    }
}
