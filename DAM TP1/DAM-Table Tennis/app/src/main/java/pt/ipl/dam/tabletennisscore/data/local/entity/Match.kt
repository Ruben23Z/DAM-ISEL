package pt.ipl.dam.tabletennisscore.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// [Entidade] Representa uma partida de Ténis de Mesa na base de dados
@Entity(
    tableName = "matches",
    // [Chaves Estrangeiras] Define a relação com a tabela de Jogadores (Players)
    foreignKeys = [
        ForeignKey(entity = Player::class, parentColumns = ["id"], childColumns = ["player1Id"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(entity = Player::class, parentColumns = ["id"], childColumns = ["player2Id"], onDelete = ForeignKey.SET_NULL)
    ],
    // [Índices] Aumentam a velocidade de pesquisa por ID de jogador
    indices = [Index("player1Id"), Index("player2Id")]
)
data class Match(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // ID único gerado automaticamente
    val player1Id: Long?, // ID do primeiro jogador (pode ser nulo se o jogador for apagado)
    val player2Id: Long?, // ID do segundo jogador
    // [Desnormalização] Guardamos o nome aqui para que o histórico sobreviva 
    // mesmo que o perfil do jogador seja apagado da App.
    val player1Name: String,
    val player2Name: String,
    val startedAt: Long = System.currentTimeMillis(), // Timestamp de início
    val finishedAt: Long = 0L, // Timestamp de fim (0 enquanto decorre)
    val winnerPlayerId: Long?, // ID de quem ganhou a partida
    val winnerName: String, // Nome de quem ganhou (para histórico rápido)
    val bestOf: Int = 5,       // Formato da partida: 3, 5 ou 7 sets
    val pointsPerSet: Int = 11 // Pontos necessários para ganhar um set (11 ou 21)
)
