package dam.exer_1

fun main() {
    val numeros = IntArray(50) //Cria array de 50 valores

    for (i in numeros.indices) {
        numeros[i] =
            (i + 1) * (i + 1) // para canda indice faz o seu valor ser o indice multiplicado por ele mesmo, ou seja, o quadrado do indice
    }

    println(numeros.joinToString(", ")) //junta e imprime
    println("------------------------------------------------------------")

    val numeros2 =
        (1..50).map { it * it } //cria uma lista de 0 a 50 e para cada valor da lista, o valor é o valor multiplicado por ele mesmo através da função map
    println(numeros2.joinToString(", "))//junta e imprime
    println("------------------------------------------------------------")

    val arr =
        Array(50) { (it + 1) * (it + 1) } //cria um array de 50 valores, onde cada valor é o indice multiplicado por ele mesmo, ou seja, o quadrado do indice
    println(arr.joinToString(", "))
}
