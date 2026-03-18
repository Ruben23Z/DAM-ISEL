package dam.exer_vl

class PhysicalBook(
    title: String,
    author: String,
    publicationYear: Int,
    availableCopies: Int,
    val weight: Int,
    val hasHardcover: Boolean = true
) : Book(title, author, publicationYear, availableCopies) {

    override fun toString(): String {
        return "PhysicalBook: ${super.toString()}"
    }

    override fun getStorageInfo(): String {
        val hc = if (hasHardcover) "Sim" else "Não"
        return "Storage: Livro fisico de ${weight}g, com hardcover $hc"
    }
}