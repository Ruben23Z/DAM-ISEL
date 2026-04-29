package damA51388.galeriaaleatoria.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import damA51388.galeriaaleatoria.model.ImageItem

/**
 * Extension 5 — Image Cache (max 50 items)
 *
 * Keeps a list of the last 50 images fetched from the API to support offline access.
 * This is separate from Favorites.
 */
class ImageCache(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("dog_cache", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val maxCacheSize = 50

    private val cachedItems = mutableListOf<ImageItem>()

    init {
        loadFromPrefs()
    }

    /**
     * Adds new items to the cache, maintaining the 50-item limit (FIFO).
     */
    fun addItems(items: List<ImageItem>) {
        items.forEach { item ->
            if (cachedItems.none { it.id == item.id }) {
                cachedItems.add(item)
            }
        }
        
        // Keep only the last 50
        while (cachedItems.size > maxCacheSize) {
            cachedItems.removeAt(0)
        }
        
        saveToPrefs()
    }

    fun getCachedItems(): List<ImageItem> = cachedItems.toList()

    private fun saveToPrefs() {
        val json = gson.toJson(cachedItems)
        prefs.edit().putString("cache_list", json).apply()
    }

    private fun loadFromPrefs() {
        val json = prefs.getString("cache_list", null)
        if (json != null) {
            val type = object : TypeToken<List<ImageItem>>() {}.type
            val list: List<ImageItem>? = try {
                gson.fromJson(json, type)
            } catch (e: Exception) {
                null
            }
            if (list != null) {
                cachedItems.clear()
                cachedItems.addAll(list)
            }
        }
    }
}
