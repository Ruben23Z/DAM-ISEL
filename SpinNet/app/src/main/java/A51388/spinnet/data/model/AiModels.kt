package A51388.spinnet.data.model

data class GeneratedRoutine(val title: String, val shots: List<GeneratedShot>)
data class GeneratedShot(
    val index: Int, val zone: Int, val spinName: String,
    val velocity: Int, val freq: String, val racketSide: String
)
