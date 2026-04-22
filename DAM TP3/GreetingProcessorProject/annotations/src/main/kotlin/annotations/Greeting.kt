package annotations
// Define o package onde esta annotation pertence.
// Isto permite importar depois com:
// import annotations.Greeting


@Target(AnnotationTarget.FUNCTION)
// Indica onde esta annotation pode ser usada.
// Neste caso apenas em funções/métodos.
//
// Exemplo válido:
// @Greeting("Olá")
// fun teste() { }
//
// Exemplo inválido:
// @Greeting("Olá")
// class Pessoa { }


@Retention(AnnotationRetention.SOURCE)
// Define quanto tempo a annotation existe.
//
// SOURCE = existe apenas durante compilação.
// Serve para annotation processors.
//
// Depois do build, desaparece do .class/.jar.
//
// Ideal para gerar código automaticamente.


annotation class Greeting(
    val message: String
)
// Declara a annotation chamada Greeting.
//
// Recebe 1 parâmetro obrigatório:
//
// message -> texto que será usado pelo processor.
//
// Exemplo:
//
// @Greeting("Hello World")
// fun sayHello() { }
//
// O processor pode ler:
// message = "Hello World"