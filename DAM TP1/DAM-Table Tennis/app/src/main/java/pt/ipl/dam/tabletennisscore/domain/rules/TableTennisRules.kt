package pt.ipl.dam.tabletennisscore.domain.rules

/**
 * [Regras de Domínio] Lógica pura de pontuação do Ténis de Mesa.
 * Esta classe é testável unitariamente pois não depende do Android.
 */
object TableTennisRules {

    /** Verifica se um jogador ganhou o set com base no seu [score], no do adversário e no limite de pontos. */
    fun hasWonSet(score: Int, opponentScore: Int, pointsPerSet: Int): Boolean {
        // Um jogador só ganha se atingir o mínimo (ex: 11) e tiver 2 pontos de vantagem
        if (score < pointsPerSet) return false
        return score - opponentScore >= 2
    }

    /** Verifica se o set está em "Deuce" (empate após o threshold, ex: 10-10). */
    fun isDeuce(score1: Int, score2: Int, pointsPerSet: Int): Boolean {
        val threshold = pointsPerSet - 1
        return score1 >= threshold && score2 >= threshold
    }

    /** Determina quem deve servir (0 ou 1) com base no total de pontos jogados. */
    fun currentServer(totalPoints: Int, isDeuce: Boolean): Int {
        return if (isDeuce) {
            // Em Deuce, o serviço muda a cada ponto
            (totalPoints % 2)
        } else {
            // Normalmente, o serviço muda a cada 2 pontos
            (totalPoints / 2) % 2
        }
    }

    /** Calcula quantos sets são necessários para ganhar a partida (ex: Best of 5 precisa de 3). */
    fun setsToWin(bestOf: Int): Int = (bestOf / 2) + 1

    /** Verifica se o número de sets ganhos é suficiente para vencer a partida. */
    fun hasWonMatch(setsWon: Int, bestOf: Int): Boolean = setsWon >= setsToWin(bestOf)
}

