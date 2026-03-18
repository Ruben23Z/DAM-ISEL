package pt.ipl.dam.tabletennisscore.domain.model

// [Modelo de Domínio] Resumo completo de uma partida para visualização no histórico
data class MatchSummary(
    val id: Long, // ID da partida
    val player1Name: String, // Nome do Jogador 1
    val player2Name: String, // Nome do Jogador 2
    val winnerName: String, // Nome do vencedor
    val setsScore: String,      // Placar final de sets (ex: "3-1")
    val durationMinutes: Long, // Duração total em minutos
    val startedAt: Long, // Quando começou
    val bestOf: Int, // Formato (ex: 5)
    val pointsPerSet: Int, // Limite de pontos (ex: 11)
    val setDetails: List<SetDetail> // Detalhes de cada set individual
)

// [Modelo de Domínio] Detalhes de um set específico para o histórico
data class SetDetail(
    val setNumber: Int, // 1º, 2º...
    val score1: Int, // Pontos J1
    val score2: Int, // Pontos J2
    val winnerName: String // Quem ganhou o set
)

