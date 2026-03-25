package Section1 // Define o pacote onde esta classe está localizada

sealed class Event { // Classe selada que representa diferentes tipos de eventos

    data class Login(val username: String, val timestamp: Long) :
        Event() // Evento de login com nome de usuário e timestamp

    data class Purchase(val username: String, val amount: Double, val timestamp: Long) :
        Event() // Evento de compra com nome de usuário, valor e timestamp

    data class Logout(val username: String, val timestamp: Long) :
        Event() // Evento de logout com nome de usuário e timestamp
}

fun List<Event>.filterByUser(username: String): List<Event> { // Extensão para filtrar eventos por usuário
    val lista = mutableListOf<Event>() // Cria uma lista mutável para armazenar os eventos filtrados
    for (event in this) { // Itera sobre cada evento na lista
        if (event is Event.Login) { // Verifica se o evento é um Login
            if (event.username == username) { // Verifica se o nome de usuário corresponde
                lista.add(event) // Adiciona o evento à lista
            }
        } else if (event is Event.Purchase) { // Verifica se o evento é uma Purchase
            if (event.username == username) { // Verifica se o nome de usuário corresponde
                lista.add(event) // Adiciona o evento à lista
            }
        } else if (event is Event.Logout) { // Verifica se o evento é um Logout
            if (event.username == username) { // Verifica se o nome de usuário corresponde
                lista.add(event) // Adiciona o evento à lista
            }
        }
    }
    return lista // Retorna a lista filtrada
}

fun List<Event>.totalSpent(username: String): Double { // Extensão para calcular o total gasto por um usuário
    var totalSpent = 0.0 // Inicializa o total gasto
    for (event in this) { // Itera sobre cada evento na lista
        if (event is Event.Purchase && event.username == username) { // Verifica se é uma compra do usuário
            totalSpent += event.amount // Adiciona o valor da compra ao total
        }
    }
    return totalSpent // Retorna o total gasto
//        return this.filterIsInstance<Event.Purchase>().filter { it.username == username }.sumOf { it.amount } // Versão alternativa comentada
}

fun processEvents(events: List<Event>, handler: (Event) -> Unit) { // Função para processar eventos com um manipulador
    for (event in events) { // Itera sobre cada evento
        handler(event) // Chama o manipulador para cada evento
    }
}

fun main() { // Função principal do programa

    val events = listOf( // Cria uma lista de eventos de exemplo
        Event.Login("alice", 1000), // Evento de login da alice no timestamp 1000
        Event.Purchase("alice", 49.99, 1100), // Compra da alice de 49.99 no timestamp 1100
        Event.Purchase("bob", 19.99, 1200), // Compra do bob de 19.99 no timestamp 1200
        Event.Login("bob", 1050), // Evento de login do bob no timestamp 1050
        Event.Purchase("alice", 15.0, 1300), // Compra da alice de 15.0 no timestamp 1300
        Event.Logout("alice", 1400), // Logout da alice no timestamp 1400
        Event.Logout("bob", 1500) // Logout do bob no timestamp 1500
    )

    processEvents(events) { event -> // Processa cada evento usando um manipulador
        when (event) { // Verifica o tipo do evento
            is Event.Login -> // Se for um login
                println("\n[LOGIN]-> ${event.username} loggin ás t=${event.timestamp}") // Imprime mensagem de login

            is Event.Purchase -> // Se for uma compra
                println("\n[PURCHASE]-> ${event.username} gastou $${event.amount} ás t=${event.timestamp}") // Imprime mensagem de compra

            is Event.Logout -> // Se for um logout
                println("\n[LOGOUT]-> ${event.username} logout ás t=${event.timestamp}") // Imprime mensagem de logout
        }
    }

    println("Total gasto pela alice: $${events.totalSpent("alice")}") // Imprime o total gasto pela alice
    println("Total gasto pelo bob: $${events.totalSpent("bob")}") // Imprime o total gasto pelo bob

    println("Eventos da alice:") // Imprime cabeçalho para eventos da alice
    println(events.filterByUser("alice")) // Imprime a lista de eventos filtrados para alice
}