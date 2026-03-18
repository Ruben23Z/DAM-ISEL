package pt.ipl.dam.tabletennisscore.domain.model

// [Modelo de Domínio] Estatísticas detalhadas de um jogador específico
data class PlayerStats(
    val playerName: String, // Nome do atleta
    val wins: Int, // Total de vitórias
    val losses: Int, // Total de derrotas
    val totalMatches: Int, // Soma de todas as partidas
    val winRate: Float, // Percentagem de vitórias (0.0 a 1.0)
    val avgPointsPerSet: Float, // Média de pontos marcados por set
    val avgPointsConcededPerSet: Float, // Média de pontos sofridos por set
    val longestWinStreak: Int, // Melhor sequência de vitórias consecutivas
    val currentStreak: Int // Sequência atual (+ positivo para vitórias, - negativo para derrotas)
)

// [Modelo de Domínio] Estatísticas globais de toda a App
data class OverallStats(
    val totalMatches: Int, // Total de partidas jogadas por todos
    val totalSets: Int, // Total de sets disputados
    val totalPoints: Int, // Soma de todos os pontos marcados em todos os jogos
    val playerStats: List<PlayerStats> // Ranking de estatísticas por jogador
)

