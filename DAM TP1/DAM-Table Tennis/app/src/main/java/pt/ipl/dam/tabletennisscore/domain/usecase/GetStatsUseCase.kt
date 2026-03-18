package pt.ipl.dam.tabletennisscore.domain.usecase

import pt.ipl.dam.tabletennisscore.data.repository.MatchRepository
import pt.ipl.dam.tabletennisscore.domain.model.OverallStats
import pt.ipl.dam.tabletennisscore.domain.model.PlayerStats
import javax.inject.Inject

// [Caso de Uso] Calcula estatísticas complexas a partir de todos os dados de partidas
class GetStatsUseCase @Inject constructor(private val repository: MatchRepository) {

    suspend operator fun invoke(): OverallStats {
        val matches = repository.getAllMatchesList()
        val allSets = repository.getAllSets()

        // Se o histórico estiver vazio, devolvemos estatísticas a zeros
        if (matches.isEmpty()) {
            return OverallStats(0, 0, 0, emptyList())
        }

        // Obtém uma lista única de todos os nomes de jogadores que já participaram
        val allPlayerNames = (matches.map { it.player1Name } + matches.map { it.player2Name }).distinct()

        // Para cada jogador, vamos calcular o seu desempenho individual
        val playerStatsList = allPlayerNames.map { playerName ->
            val playerMatches = matches.filter { it.player1Name == playerName || it.player2Name == playerName }
                .sortedBy { it.startedAt }
            
            val wins = playerMatches.count { it.winnerName == playerName }
            val losses = playerMatches.size - wins
            val winRate = if (playerMatches.isNotEmpty()) wins.toFloat() / playerMatches.size else 0f

            val matchIds = playerMatches.map { it.id }.toSet()
            val playerSets = allSets.filter { it.matchId in matchIds }
            
            // Soma todos os pontos marcados pelo jogador em todos os seus sets
            val totalPointsScored = playerSets.sumOf { set ->
                val match = playerMatches.first { it.id == set.matchId }
                if (match.player1Name == playerName) set.score1 else set.score2
            }
            
            // Soma todos os pontos que o jogador sofreu
            val totalPointsConceded = playerSets.sumOf { set ->
                val match = playerMatches.first { it.id == set.matchId }
                if (match.player1Name == playerName) set.score2 else set.score1
            }

            val avgPointsPerSet = if (playerSets.isNotEmpty()) totalPointsScored.toFloat() / playerSets.size else 0f
            val avgConcededPerSet = if (playerSets.isNotEmpty()) totalPointsConceded.toFloat() / playerSets.size else 0f

            // [Lógica] Cálculo de Sequências (Streaks)
            var maxWinStreak = 0 // Melhor sequência de sempre
            var currentStreak = 0 // Sequência consecutiva atual
            
            playerMatches.forEach { match ->
                if (match.winnerName == playerName) {
                    // Se ganhou, aumenta a win streak
                    if (currentStreak >= 0) {
                        currentStreak++
                    } else {
                        currentStreak = 1
                    }
                    if (currentStreak > maxWinStreak) maxWinStreak = currentStreak
                } else {
                    // Se perdeu, altera para loss streak (número negativo)
                    if (currentStreak <= 0) {
                        currentStreak--
                    } else {
                        currentStreak = -1
                    }
                }
            }

            PlayerStats(
                playerName = playerName,
                wins = wins,
                losses = losses,
                totalMatches = playerMatches.size,
                winRate = winRate,
                avgPointsPerSet = avgPointsPerSet,
                avgPointsConcededPerSet = avgConcededPerSet,
                longestWinStreak = maxWinStreak,
                currentStreak = currentStreak
            )
        }

        // Devolve as estatísticas globais somadas e os jogadores ordenados por vitórias
        return OverallStats(
            totalMatches = matches.size,
            totalSets = allSets.size,
            totalPoints = allSets.sumOf { it.score1 + it.score2 },
            playerStats = playerStatsList.sortedByDescending { it.wins }
        )
    }
}

