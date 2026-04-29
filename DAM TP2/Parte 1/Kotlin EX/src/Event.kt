package Section1 // Define o pacote onde esta classe está localizada

sealed class Event { // Classe selada que representa diferentes tipos de eventos

    data class Login(val username: String, val timestamp: Long) :
        Event()

    data class Purchase(val username: String, val amount: Double, val timestamp: Long) :
        Event()

    data class Logout(val username: String, val timestamp: Long) :
        Event()
}

fun List<Event>.filterByUser(username: String): List<Event> { // Extensão para filtrar eventos por usuário
    val lista = mutableListOf<Event>() // Cria uma lista mutável para armazenar os eventos filtrados
    for (event in this) { // Itera sobre cada evento na lista
        if (event is Event.Login) {
            if (event.username == username) {
                lista.add(event)
            }
        } else if (event is Event.Purchase) {
            if (event.username == username) {
                lista.add(event)
            }
        } else if (event is Event.Logout) {
            if (event.username == username) {
                lista.add(event)
            }
        }
    }
    return lista
}

fun List<Event>.totalSpent(username: String): Double { // Extensão para calcular o total gasto por um usuário
    var totalSpent = 0.0
    for (event in this) {
        if (event is Event.Purchase && event.username == username) {
            totalSpent += event.amount
        }
    }
    return totalSpent // Retorna o total gasto
//        return this.filterIsInstance<Event.Purchase>().filter { it.username == username }.sumOf { it.amount } // Versão alternativa comentada
}

fun processEvents(events: List<Event>, handler: (Event) -> Unit) { // Função para processar eventos com um manipulador
    for (event in events) {
        handler(event) // Chama o manipulador para cada evento
    }
}

fun main() { // Função principal do programa

    val events = listOf(
        Event.Login("alice", 1000),
        Event.Purchase("alice", 49.99, 1100),
        Event.Purchase("bob", 19.99, 1200),
        Event.Login("bob", 1050),
        Event.Purchase("alice", 15.0, 1300),
        Event.Logout("alice", 1400),
        Event.Logout("bob", 1500)
    )

    processEvents(events) { event -> // Processa cada evento usando um manipulador
        when (event) {
            is Event.Login ->
                println("\n[LOGIN]-> ${event.username} loggin ás t=${event.timestamp}")

            is Event.Purchase -> // Se for uma compra
                println("\n[PURCHASE]-> ${event.username} gastou $${event.amount} ás t=${event.timestamp}")

            is Event.Logout ->
                println("\n[LOGOUT]-> ${event.username} logout ás t=${event.timestamp}")
        }
    }

    println("Total gasto pela alice: $${events.totalSpent("alice")}")
    println("Total gasto pelo bob: $${events.totalSpent("bob")}")

    println("Eventos da alice:")
    println(events.filterByUser("alice"))
}