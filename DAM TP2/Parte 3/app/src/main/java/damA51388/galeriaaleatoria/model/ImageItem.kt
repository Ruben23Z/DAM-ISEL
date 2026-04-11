package damA51388.galeriaaleatoria.model

import java.io.Serializable

/**
 * Data Model — Dog Image (docs/04_data_model.md)
 *
 * Implements Serializable to be passed between activities.
 */
data class ImageItem(
    val id: String,
    val url: String,
    val breed: String,
    var isLiked: Boolean = false,
    var isFavorite: Boolean = false
) : Serializable {

    val displayBreed: String
        get() = breed
            .split("-")
            .joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }

    companion object {
        fun fromUrl(url: String): ImageItem {
            val breed = runCatching {
                val parts = url.split("/")
                val breedsIndex = parts.indexOf("breeds")
                if (breedsIndex >= 0) parts[breedsIndex + 1] else "unknown"
            }.getOrDefault("unknown")

            return ImageItem(
                id    = url.hashCode().toString(),
                url   = url,
                breed = breed
            )
        }
    }
}
