package A51388.spinnet.data.model

data class TrainingPlan(
    val id: String = "",
    val title: String = "",
    val uid: String = "",
    val description: String = "",
    val routines: List<PlanRoutineItem> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val isPublic: Boolean = false,
    val workTimeSeconds: Int = 60,
    val restTimeSeconds: Int = 15
)

data class PlanRoutineItem(
    val routineId: String = "",
    val routineTitle: String = "",
    val durationMinutes: Int = 5,
    val durationSeconds: Int = 60,
    val restSeconds: Int = 15,
    val shotsCount: Int = 0
)
