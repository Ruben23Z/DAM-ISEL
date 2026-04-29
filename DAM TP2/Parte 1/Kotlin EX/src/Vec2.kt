// Declara o pacote onde esta classe está localizada
package Section1

// Define uma classe de dados Vec2 que representa um vetor 2D com coordenadas x e y.
data class Vec2(val x: Double, val y: Double) : Comparable<Vec2> {
    // Sobrescreve o método compareTo para comparar vetores pela sua magnitude (comprimento).
    // Isso permite usar operadores como >, <, >=, <= entre vetores, comparando seus tamanhos.
    override fun compareTo(other: Vec2): Int {
        // Retorna a comparação das magnitudes: chama magnitude() para calcular o comprimento de cada vetor e compara os valores Double.
        //Retorna um Int com a convenção padrão: negativo se this < other, zero se iguais, positivo se this > other.
        return this.magnitude().compareTo(other.magnitude())
    }
}

operator fun Vec2.plus(other: Vec2): Vec2 {
    return Vec2(this.x + other.x, this.y + other.y)
}


operator fun Vec2.minus(other: Vec2): Vec2 {
    return Vec2(this.x - other.x, this.y - other.y)
}

// Define o operador * para multiplicar um vetor por um escalar (Double).
// Permite escrever v * 2.0 para escalar o vetor
operator fun Vec2.times(other: Double): Vec2 {
    return Vec2(this.x * other, this.y * other)
}

// Necessário porque Kotlin não permite comutatividade automática; permite escrever 2.0 * v.
operator fun Double.times(other: Vec2): Vec2 {
    return Vec2(this * other.x, this * other.y)
}

operator fun Vec2.unaryMinus(): Vec2 {
    return Vec2(-this.x, -this.y)
}

// Define o operador get para acessar coordenadas por índice (0 para x, 1 para y).
// Permite escrever v[0] para x e v[1] para y, como um array, facilitando iteração ou acesso genérico.
operator fun Vec2.get(index: Int): Double {
    if (index == 0) return this.x
    else if (index == 1) return this.y
    else throw IndexOutOfBoundsException("Index out of bounds: $index")
}

//permte fazer isto val (x, y) = v de forma facil
operator fun Vec2.component1() = this.x
operator fun Vec2.component2() = this.y


fun Vec2.magnitude(): Double {
    return Math.sqrt(this.x * this.x + this.y * this.y)
}


fun Vec2.dot(other: Vec2): Double {
    return this.x * other.x + this.y * other.y
}


fun Vec2.normalized(): Vec2 {
    val mag = this.magnitude()
    if (mag == 0.0) throw IllegalStateException("Cannot normalize zero vector")
    return Vec2(this.x / mag, this.y / mag)

}


fun main() {
    val a = Vec2(3.0, 4.0)
    val b = Vec2(1.0, 2.0)
    println("a = $a") // a = Vec2 (x=3.0 , y =4.0)
    println("b = $b") // b = Vec2 (x=1.0 , y =2.0)
    println("a + b = ${a + b}") // a + b = Vec2 (x=4.0 , y =6.0)
    println("a - b = ${a - b}") // a - b = Vec2 (x=2.0 , y =2.0)
    println("a * 2.0 = ${a * 2.0} ") // a * 2.0 = Vec2 (x=6.0 , y =8.0)
    println("-a = ${-a}") // -a = Vec2 (x= -3.0 , y= -4.0)
    println("|a| = ${a.magnitude()}") // |a| = 5.0
    println("a dot b = ${a.dot(b)}") // a dot b = 11.0
    println(" norm (a) = ${a.normalized()}")
// norm (a) = Vec2 (x=0.6 , y =0.8)
    println("a[0] = ${a[0]} ") // a[0] = 3.0
    println("a[1] = ${a[1]} ") // a[1] = 4.0
    println("a > b = ${a > b}") // a > b = true
    println("a < b = ${a < b}") // a < b = false
    val vectors = listOf(Vec2(1.0, 0.0), Vec2(3.0, 4.0), Vec2(0.0, 2.0))
    println("Longest = ${vectors.maxOrNull()}")// Longest = Vec2 (x=3.0 , y =4.0)
    //
    println("Shortest = ${vectors.minOrNull()}")
    //Shortest = Vec2 (x=1.0 ,    y =0.0)
}