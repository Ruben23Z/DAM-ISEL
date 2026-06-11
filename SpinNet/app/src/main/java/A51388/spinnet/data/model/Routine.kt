package A51388.spinnet.data.model


data class Routine(
    val id: String = "",
    val title: String = "",
    val uid: String = "",
    val shots: List<Shot> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val scheduledAt: Long? = null,
    val isPublic: Boolean = false //para feed

)

data class Shot(
    val index: Int = 0,
    val zone: Int = 0,
    val spinName: String = "",
    val velocity: Int = 0,
    val freq: String = ""
)
