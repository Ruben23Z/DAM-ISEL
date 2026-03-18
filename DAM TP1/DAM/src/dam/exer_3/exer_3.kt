package dam.exer_3

fun main() {

    val bounceSequence = generateSequence(100.0) { it * 0.6 }
        .filter { it >= 1.0 }
        .take(15)
        .map { "%.2f".format(it) } // Format to 2 decimal places
        .toList()

    println("Sequencia de altura de Bounce:")
    println(bounceSequence)
}
