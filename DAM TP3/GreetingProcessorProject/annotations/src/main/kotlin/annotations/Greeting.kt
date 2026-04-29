package annotations

//PROF

//define o @Greting que somente pode ser usada em funcoes e não classes
@Target ( AnnotationTarget . FUNCTION )
@Retention ( AnnotationRetention . SOURCE ) //define que só existe no codigo fonte, em runtime
annotation class Greeting (val message : String )
//permite passar a mensagem de greeting