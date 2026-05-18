package annotations

//MEEE

//define o @extract que somente pode ser usada em funcoes e não classes
@Target ( AnnotationTarget . FUNCTION )
@Retention ( AnnotationRetention . SOURCE ) //define que só existe no codigo fonte, em runtime
annotation class Extract (val regex : String )
