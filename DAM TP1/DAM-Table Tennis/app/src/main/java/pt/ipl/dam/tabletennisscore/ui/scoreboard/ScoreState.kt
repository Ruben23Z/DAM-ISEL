package pt.ipl.dam.tabletennisscore.ui.scoreboard

import pt.ipl.dam.tabletennisscore.domain.rules.TableTennisRules

// [Estado da UI] Objeto imutável que representa tudo o que vemos no ecrã do marcador
data class ScoreState(
    val player1Name: String = "Player 1",
    val player2Name: String = "Player 2",
    val score1: Int = 0, // Pontos atuais do J1
    val score2: Int = 0, // Pontos atuais do J2
    val sets1: Int = 0, // Sets ganhos pelo J1
    val sets2: Int = 0, // Sets ganhos pelo J2
    val currentSet: Int = 1, // Número do set a decorrer
    val bestOf: Int = 5, // Formato (ex: à melhor de 5)
    val pointsPerSet: Int = 11, // Pontos para fechar set
    val server: Int = 0,          // Quem serve (0 para J1, 1 para J2)
    val isDeuce: Boolean = false, // Se estamos em situação de empate nas vantagens
    val matchFinished: Boolean = false, // Se a partida acabou totalmente
    val setJustFinished: Boolean = false, // Se um set acabou de fechar (trigger para animação)
    val setWinnerName: String = "", // Nome de quem ganhou o último set
    val matchWinnerName: String = "", // Nome de quem ganhou a partida
    val completedSets: List<CompletedSet> = emptyList() // Lista de sets que já acabaram
)

/** Representa um set que já foi concluído (usado para salvar depois) */
data class CompletedSet(
    val setNumber: Int,
    val score1: Int,
    val score2: Int,
    val winnerName: String
)

/** Eventos que a UI pode enviar para o ViewModel mudar o estado */
sealed class ScoreEvent {
    data class AddPoint(val playerIndex: Int) : ScoreEvent() // Adicionar ponto (0 ou 1)
    object UndoLastPoint : ScoreEvent() // Anular a última ação
    object AcknowledgeSetEnd : ScoreEvent() // Utilizador confirmou que viu a vitória do set
    object AcknowledgeMatchEnd : ScoreEvent() // Utilizador confirmou o fim da partida
}

