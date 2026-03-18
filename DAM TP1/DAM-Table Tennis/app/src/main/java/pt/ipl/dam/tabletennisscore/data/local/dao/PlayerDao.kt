package pt.ipl.dam.tabletennisscore.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pt.ipl.dam.tabletennisscore.data.local.entity.Player

// [DAO] Interface para gerir os perfis dos Jogadores
@Dao
interface PlayerDao {
    // Retorna todos os jogadores por ordem alfabética
    @Query("SELECT * FROM players ORDER BY name ASC")
    fun getAllPlayers(): Flow<List<Player>>

    // Insere um novo jogador. Se já existir um com o mesmo nome, ignora (evita duplicados).
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(player: Player): Long

    // Atualiza os dados de um jogador existente
    @Update
    suspend fun update(player: Player)

    // Apaga um jogador
    @Delete
    suspend fun delete(player: Player)

    // Procura um jogador específico pelo nome
    @Query("SELECT * FROM players WHERE name = :name LIMIT 1")
    suspend fun findByName(name: String): Player?
}

