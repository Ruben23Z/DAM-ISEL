package annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)

//eu
annotation class Extract(
    val regex: String
)