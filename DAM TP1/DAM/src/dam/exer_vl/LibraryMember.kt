package dam.exer_vl

// 'data class' é usada para classes cujo único propósito é guardar dados.
// O Kotlin gera automaticamente métodos como: toString(), equals(), hashCode() e copy().
data class LibraryMember(
    // Propriedade imutável (val) para o nome do membro.
    val name: String,
    
    // Identificador único (ID) do membro.
    val membershipId: String,
    
    // Lista mutável de Strings que guarda os títulos dos livros que este membro tem.
    // '= mutableListOf()' define um valor por defeito: se não passarmos nada ao criar o objeto,
    // ele começa com uma lista vazia automaticamente.
    val borrowedBooks: MutableList<String> = mutableListOf()
)
