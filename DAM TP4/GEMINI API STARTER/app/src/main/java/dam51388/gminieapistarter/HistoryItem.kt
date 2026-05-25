package dam51388.gminieapistarter

data class HistoryItem(
    val id: String,
    val timestamp: String,
    val imageName: String, // "cookies", "cake_slice", "cake_full", "custom"
    val customImageUriString: String?,
    val prompt: String,
    val response: String
)
