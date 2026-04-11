package damA51388.galeriaaleatoria.model

/**
 * Data Model — Dog Image (docs/04_data_model.md)
 *
 * The Dog CEO API returns only an image URL.
 * We derive the breed name by parsing the URL path segment.
 *
 * Example URL: https://images.dog.ceo/breeds/hound-afghan/photo.jpg
 *   → breed = "hound-afghan"
 *   → displayBreed = "Hound Afghan"
 */
data class ImageItem(
    val id: String,          // unique: URL-based hash
    val url: String,         // full image URL from the API
    val breed: String,       // e.g. "hound-afghan"
    var isLiked: Boolean = false,
    var isFavorite: Boolean = false
) {
    /** Human-readable breed: "hound-afghan" → "Hound Afghan" */
    val displayBreed: String
        get() = breed
            .split("-")
            .joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }

    companion object {
        /**
         * Build an [ImageItem] from a raw Dog CEO image URL.
         * Extracts the breed from the URL path: .../breeds/<breed>/filename.jpg
         */
        fun fromUrl(url: String): ImageItem {
            val breed = runCatching {
                // path looks like: /breeds/hound-afghan/n02088094_1003.jpg
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
