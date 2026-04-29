package damA51388.core.repository

import android.content.Context
import damA51388.core.model.ImageItem
import damA51388.core.network.DogApiService
import damA51388.core.storage.ImageCache

/**
 * Extension 6 — Repository with Offline Support
 */
class ImageRepository(
    private val api: DogApiService,
    private val cache: ImageCache
) {

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
