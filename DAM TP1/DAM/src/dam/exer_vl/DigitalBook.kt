package dam.exer_vl

// A classe 'DigitalBook' é uma subclasse de 'Book'.
// Os parâmetros title, author, publicationYear e availableCopies NÃO têm val/var aqui porque
// são apenas repassados para a classe mãe (Book).
// fileSize e format SÃO declarados com 'val' porque são propriedades únicas desta classe.
class DigitalBook(
    title: String, 
    author: String, 
    publicationYear: Int, 
    availableCopies: Int, 
    val fileSize: Double, 
    val format: String
) : Book(title, author, publicationYear, availableCopies) { // Faz a herança chamando o construtor da classe base 'Book'


    // 'override' indica que estamos a dar uma nova implementação a um método que já existe na classe mãe.
    override fun toString(): String {
        // 'super.toString()' chama a implementação de 'toString()'da classe 'Book'.
        return "DigitalBook: ${super.toString()}"
    }

    // Aqui somos OBRIGADOS a usar 'override' porque 'getStorageInfo' foi declarado como 'abstract' na classe 'Book'.
    override fun getStorageInfo(): String {
        // Esta implementação é específica para livros digitais, mostrando o tamanho e formato.
        return "Storage: Digitalmente guardado: $fileSize MB, Formato: $format"
    }
}
