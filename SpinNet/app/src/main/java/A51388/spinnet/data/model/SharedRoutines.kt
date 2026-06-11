package A51388.spinnet.data.model


data class SharedRoutine(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val shots: List<Shot> = emptyList(),
    val createdAt: Long = 0L,
    val isPublic: Boolean = false,
    val sharedWith: List<String> = emptyList(),
    val sharedBy: String = ""
)
