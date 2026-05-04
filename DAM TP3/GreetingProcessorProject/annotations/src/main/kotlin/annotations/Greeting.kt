package annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)



//EU FIZ
annotation class Greeting(
    val message: String
)
