package dam.exer_vl

class Library(val name: String) {

    // Criamos uma lista mutável que guarda objetos do tipo 'Book'.
    // Como 'Book' é a classe base, esta lista aceita tanto DigitalBook como PhysicalBook
    // (Polimorfismo!).
    val books = mutableListOf<Book>()
    fun addBook(book: Book) {
        books.add(book) // adicona livros a lista
        incrementBooks() // Chama o método do 'companion object' para atualizar o contador global.
        println("\n Titulo: '${book.title}'por ${book.author} foi adicionado á biblioteca.")
    }

    fun borrowBook(title: String) {
        val bookDecrease =
                books.find {
                    it.title == title
                } // Procura um livro na lista com o título fornecido.

        if (bookDecrease != null && bookDecrease.availableCopies > 0) {
            bookDecrease.availableCopies--
            println(
                    "\nLivro requesitado'$title' com sucesso. Nº de cópias restantes: ${bookDecrease.availableCopies}"
            )
        } else if (bookDecrease != null && bookDecrease.availableCopies == 0) {
            println("AVISO: Livro fora de stock!")
            println("Sorry, '$title' não pode ser emprestado.")
        } else {
            println("Book '$title' não encontrado na biblio.")
        }
    }

    fun returnBook(title: String) {
        val book =
                books.find {
                    it.title == title
                } // Procura um livro na lista com o título fornecido.

        if (book != null) {
            book.availableCopies++ // Incrementa o número de cópias disponíveis.
            println(
                    "\n DEVOLUÇÃO Livro '$title' devolvido. Copias disponiveis: ${book.availableCopies}"
            )
        } else {
            println("\nLivro '$title' não encontrado na biblio.")
        }
    }
    fun showBooks() {
        println("\n--- Catalogo da Biblioteca ---")

        //        for (book in books) {
        //            println("Titlo: ${book.title}, Autor: ${book.author}, Ano de publicação:
        // ${book.publicationCategory}, nº de copias: ${book.availableCopies}")
        //        }
        for (book in books) {
            println(book)
            // Aqui chamamos o método abstrato que cada tipo de livro implementou à sua maneira.
            println(book.getStorageInfo())
        }
    }

    fun searchByAuthor(author: String) {
        val results = books.filter { it.author == author } // Filtra os livros por autor.
        println("\nLivros por $author:")
        if (results.isEmpty()) {
            println("Sem livros do autor.")
        } else {
            for (book in results) {
                println(
                        "- ${book.title} (${book.publicationCategory}, ${book.availableCopies} cópias disponiveis)"
                )
            }
        }
    }

    // O 'companion object' funciona como a parte static da classe.
    // Tudo aqui dentro pertence à classe em si e não a um objeto específico.
    companion object {
        private var totalBooksCreated = 0 
        // Contador privado para saber quantos livros já foram criados no total.

        fun getTotalBooksCreated(): Int {
            return totalBooksCreated
        }

        // Método para aumentar o contador (chamado sempre que um livro é adicionado).
        fun incrementBooks() {
            totalBooksCreated++
        }
    }
}
