package Section1 // Define o pacote onde esta classe está localizada
class Cache<K : Any, V : Any> { // Classe genérica Cache que armazena pares chave-valor, onde K e V são tipos não nulos

    private val map = mutableMapOf<K, V>() // Mapa mutável privado que armazena os dados da cache

    fun put(key: K, value: V) { // Função para inserir ou atualizar um valor na cache
        map.put(key, value) // Insere a chave e o valor no mapa
    }

    fun get(key: K): V? { // Função para obter um valor da cache pela chave
        return map[key] // Retorna o valor associado à chave, ou null se não existir
    }

    fun evict(key: K) { // Função para remover um valor da cache pela chave
        map.remove(key) // Remove a entrada do mapa
    }

    fun size(): Int { // Função para obter o tamanho da cache
        return map.size // Retorna o número de entradas no mapa
    }

    fun getOrPut(
        key: K, default: () -> V
    ): V { // Função que obtém o valor se existir, ou calcula e insere um valor padrão
        return map.getOrPut(key, default) // Usa o método getOrPut do mapa para implementar a lógica
    }

    fun transform(key: K, action: (V) -> V): Boolean { // Função para transformar um valor existente na cache
        val value = map[key] // Obtém o valor atual
        if (value != null) { // Se o valor existir
            map[key] = action(value) // Aplica a ação de transformação e atualiza o mapa
            return true // Retorna true indicando sucesso
        }
        return false // Retorna false se a chave não existir
    }

    fun snapshot(): Map<K, V> { // Função para obter uma cópia imutável do estado atual da cache
        return map.toMap() // Retorna uma cópia do mapa como Map imutável
    }

    fun filterValues(predicate: (V) -> Boolean): Map<K, V> { // Função para filtrar valores da cache com base em um predicado
        return map.filterValues(predicate).toMap() // Filtra os valores e retorna uma cópia imutável
    }
}

fun main() {

    println("--- Word frequency cache ---") // Imprime o cabeçalho para a demonstração de cache de frequência de palavras

    val wordCache = Cache<String, Int>() // Cria uma instância de Cache para String e Int

    wordCache.put("kotlin", 1) // Insere a palavra "kotlin" com frequência 1
    wordCache.put("scala", 1) // Insere a palavra "scala" com frequência 1
    wordCache.put("haskell", 1) // Insere a palavra "haskell" com frequência 1

    println("Size: ${wordCache.size()}") // Imprime o tamanho da cache

    println("Frequency of \"kotlin\": ${wordCache.get("kotlin")}") // Imprime a frequência de "kotlin"

    println("getOrPut \"kotlin\": ${wordCache.getOrPut("kotlin") { 0 }}") // Tenta obter "kotlin", já existe
    println("getOrPut \"java\": ${wordCache.getOrPut("java") { 0 }}") // Tenta obter "java", não existe, insere 0

    println("Size after getOrPut: ${wordCache.size()}") // Imprime o tamanho após getOrPut

    println("Transform \"kotlin\" (+1): ${wordCache.transform("kotlin") { it + 1 }}") // Transforma a frequência de "kotlin" incrementando 1
    println("Transform \"cobol\" (+1): ${wordCache.transform("cobol") { it + 1 }}") // Tenta transformar "cobol", que não existe

    println("Snapshot: ${wordCache.snapshot()}") // Imprime um snapshot da cache

    // Challenge
    val filtered = wordCache.filterValues { it > 0 } // Filtra valores maiores que 0
    println("Filtered: $filtered") // Imprime os valores filtrados

    println("\n--- Id registry cache ---") // Imprime o cabeçalho para a demonstração de cache de registro de IDs

    val idCache = Cache<Int, String>() // Cria uma instância de Cache para Int e String

    idCache.put(1, "Alice") // Insere ID 1 com nome "Alice"
    idCache.put(2, "Bob") // Insere ID 2 com nome "Bob"

    println("Id 1 -> ${idCache.get(1)}") // Imprime o nome associado ao ID 1
    println("Id 2 -> ${idCache.get(2)}") // Imprime o nome associado ao ID 2

    idCache.evict(1) // Remove o ID 1 da cache

    println("After evict id 1, size: ${idCache.size()}") // Imprime o tamanho após remoção
    println("Id 1 after evict -> ${idCache.get(1)}") // Tenta obter ID 1 após remoção
}