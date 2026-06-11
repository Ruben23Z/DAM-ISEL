package A51388.spinnet.data.model


data class TrainingSession(
    val id: String = "",
    val uid: String = "",
    val routineId: String = "",
    val routineTitle: String = "",
    val durationMinutes: Int = 0,
    val reps: Int = 0,
    val accuracy: Int = 0,
    val completedAt: Long = 0L,
    val racketSide: String = ""
)