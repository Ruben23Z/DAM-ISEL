// Declara o pacote onde esta classe está localizada
package Section1

// Define uma classe de dados Vec2 que representa um vetor 2D com coordenadas x e y.
// Usa 'data class' porque gera automaticamente métodos como equals, hashCode, toString, copy, e destructuring.
// Implementa Comparable<Vec2> para permitir comparações entre vetores, útil para ordenação ou encontrar máximo/mínimo.
data class Vec2(val x: Double, val y: Double) : Comparable<Vec2> {
    // Sobrescreve o método compareTo para comparar vetores pela sua magnitude (comprimento).
    // Isso permite usar operadores como >, <, >=, <= entre vetores, comparando seus tamanhos.
    override fun compareTo(other: Vec2): Int {
        // Retorna a comparação das magnitudes: chama magnitude() para calcular o comprimento de cada vetor e compara os valores Double.
        return this.magnitude().compareTo(other.magnitude())
    }
}

// Define o operador + para somar dois vetores Vec2.
// Usa 'operator fun' para sobrecarga de operador, permitindo escrever a + b em vez de a.plus(b), tornando o código mais intuitivo e matemático.
operator fun Vec2.plus(other: Vec2): Vec2 {
    // Retorna um novo vetor com as coordenadas somadas: x + x e y + y.
    return Vec2(this.x + other.x, this.y + other.y)
}

// Define o operador - para subtrair dois vetores Vec2.
// Similar ao +, usa sobrecarga para subtração vetorial.
operator fun Vec2.minus(other: Vec2): Vec2 {
    // Retorna um novo vetor com as coordenadas subtraídas: x - x e y - y.
    return Vec2(this.x - other.x, this.y - other.y)
}

// Define o operador * para multiplicar um vetor por um escalar (Double).
// Permite escrever v * 2.0 para escalar o vetor, comum em gráficos e física.
operator fun Vec2.times(other: Double): Vec2 {
    // Retorna um novo vetor com cada coordenada multiplicada pelo escalar.
    return Vec2(this.x * other, this.y * other)
}

// Define o operador * para multiplicar um escalar por um vetor (ordem reversa).
// Necessário porque Kotlin não permite comutatividade automática; permite escrever 2.0 * v.
operator fun Double.times(other: Vec2): Vec2 {
    // Retorna um novo vetor com o escalar multiplicado por cada coordenada.
    return Vec2(this * other.x, this * other.y)
}

// Define o operador unário - para negar um vetor (inverter sinal).
// Usa 'unaryMinus' para -v, útil para vetores opostos.
operator fun Vec2.unaryMinus(): Vec2 {
    // Retorna um novo vetor com sinais invertidos: -x e -y.
    return Vec2(-this.x, -this.y)
}

// Define o operador get para acessar coordenadas por índice (0 para x, 1 para y).
// Permite escrever v[0] para x e v[1] para y, como um array, facilitando iteração ou acesso genérico.
operator fun Vec2.get(index: Int): Double {
    // Se o índice for 0, retorna a coordenada x.
    if (index == 0) return this.x
    // Se o índice for 1, retorna a coordenada y.
    else if (index == 1) return this.y
    // Caso contrário, lança uma exceção porque índices fora de 0 ou 1 não são válidos para um vetor 2D.
    else throw IndexOutOfBoundsException("Index out of bounds: $index")
}

// Define uma função para calcular a magnitude (comprimento) do vetor.
// A magnitude é a raiz quadrada da soma dos quadrados das coordenadas, fórmula padrão da geometria euclidiana.
fun Vec2.magnitude(): Double {
    // Retorna Math.sqrt(x² + y²), que é a distância do vetor à origem.
    return Math.sqrt(this.x * this.x + this.y * this.y)
}

// Define uma função para calcular o produto escalar (dot product) entre dois vetores.
// O dot product é usado para medir o ângulo entre vetores ou projeções, fórmula: x1*x2 + y1*y2.
fun Vec2.dot(other: Vec2): Double {
    // Retorna a soma dos produtos das coordenadas correspondentes.
    return this.x * other.x + this.y * other.y
}

// Define uma função para normalizar o vetor (torná-lo unitário, magnitude 1).
// Normalização é útil para direções, divide cada coordenada pela magnitude.
fun Vec2.normalized(): Vec2 {
    // Calcula a magnitude do vetor.
    val mag = this.magnitude()
    // Se a magnitude for zero, lança exceção porque não é possível dividir por zero e normalizar um vetor zero não faz sentido.
    if (mag == 0.0) throw IllegalStateException("Cannot normalize zero vector")
    // Retorna um novo vetor com coordenadas divididas pela magnitude, resultando em magnitude 1.
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