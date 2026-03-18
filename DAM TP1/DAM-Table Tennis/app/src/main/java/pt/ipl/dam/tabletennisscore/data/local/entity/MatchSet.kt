package pt.ipl.dam.tabletennisscore.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// [Entidade] Representa um "Set" individual dentro de uma partida
@Entity(
    tableName = "match_sets",
    foreignKeys = [
        // [CASCADE] Se a partida (Match) for apagada, todos os seus sets são apagados automaticamente
        ForeignKey(entity = Match::class, parentColumns = ["id"], childColumns = ["matchId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("matchId")]
)

data class MatchSet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // ID único do set
    val matchId: Long, // Ligação à partida pai
    val setNumber: Int, // Número do set (1º, 2º, 3º...)
    val score1: Int, // Pontos do Jogador 1 neste set
    val score2: Int, // Pontos do Jogador 2 neste set
    val winnerName: String, // Nome de quem ganhou este set específico
    val durationSeconds: Long = 0L // Quanto tempo demorou o set (em segundos)
)

