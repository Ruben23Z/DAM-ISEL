package damA51388.galeriaaleatoria.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import damA51388.galeriaaleatoria.model.ImageItem

/**
 * Extension 4 — Favourite Items (FIFO queue, max 5)
 *
 * Persists the top 5 favorite images using SharedPreferences.
 */
class FavoritesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("dog_favorites", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val favorites = mutableListOf<ImageItem>()

    init {
        loadFavorites()
    }

    /**
     * Adds an item to favorites.
     * Uses FIFO logic: if size reaches 6, removes the oldest (index 0).
     */
    fun toggleFavorite(item: ImageItem): Boolean {
        val existingIndex = favorites.indexOfFirst { it.id == item.id }
        
        return if (existingIndex != -1) {
            // Already a favorite -> remove it
            favorites.removeAt(existingIndex)
            saveFavorites()
            false
        } else {
            // New favorite -> add to end
            favorites.add(item)
            
            // FIFO logic: max 5
            if (favorites.size > 5) {
                favorites.removeAt(0)
            }
            
            saveFavorites()
            true
        }
    }

    fun isFavorite(itemId: String): Boolean {
        return favorites.any { it.id == itemId }
    }

    fun getFavorites(): List<ImageItem> = favorites.toList()

    private fun saveFavorites() {
        val json = gson.toJson(favorites)
        prefs.edit().putString("favorites_list", json).apply()
    }

    private fun loadFavorites() {
        val json = prefs.getString("favorites_list", null)
        if (json != null) {
            val type = object : TypeToken<List<ImageItem>>() {}.type
            val list: List<ImageItem>? = try {
                gson.fromJson(json, type)
            } catch (e: Exception) {
                null
            }
            if (list != null) {
                favorites.clear()
                favorites.addAll(list)
            }
        }
    }
}
