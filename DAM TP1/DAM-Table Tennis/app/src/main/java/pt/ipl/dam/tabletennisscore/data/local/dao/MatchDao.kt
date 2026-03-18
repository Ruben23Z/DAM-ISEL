package pt.ipl.dam.tabletennisscore.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pt.ipl.dam.tabletennisscore.data.local.entity.Match
import pt.ipl.dam.tabletennisscore.data.local.entity.MatchSet
import pt.ipl.dam.tabletennisscore.data.local.relation.MatchWithSets

// [DAO] Interface para aceder aos dados das Partidas (Matches) e Sets
@Dao
interface MatchDao {

    // Retorna um Flow de partidas com os seus respectivos sets. 
    // O Flow permite que a UI se atualize automaticamente quando os dados mudam.
    @Transaction
    @Query("SELECT * FROM matches ORDER BY startedAt DESC")
    fun getAllMatchesWithSets(): Flow<List<MatchWithSets>>

    // Procura uma partida individual pelo ID
    @Query("SELECT * FROM matches WHERE id = :matchId")
    suspend fun getMatch(matchId: Long): Match?

    // Retorna uma única partida com os seus sets (usado nos detalhes)
    @Transaction
    @Query("SELECT * FROM matches WHERE id = :matchId")
    suspend fun getMatchWithSets(matchId: Long): MatchWithSets?

    // Insere uma nova partida e devolve o ID gerado
    @Insert
    suspend fun insertMatch(match: Match): Long

    // Insere um novo Set associado a uma partida
    @Insert
    suspend fun insertSet(set: MatchSet): Long

    // Apaga uma partida da base de dados
    @Query("DELETE FROM matches WHERE id = :matchId")
    suspend fun deleteMatch(matchId: Long)

    // Calcula o total de vitórias para um jogador específico
    @Query("SELECT COUNT(*) FROM matches WHERE winnerName = :playerName")
    suspend fun getWinsForPlayer(playerName: String): Int

    // Calcula o total de derrotas para um jogador (partidas onde participou mas não ganhou)
    @Query("SELECT COUNT(*) FROM matches WHERE (player1Name = :playerName OR player2Name = :playerName) AND winnerName != :playerName")
    suspend fun getLossesForPlayer(playerName: String): Int

    // Lista simples de todas as partidas (sem os sets)
    @Query("SELECT * FROM matches ORDER BY startedAt DESC")
    suspend fun getAllMatchesList(): List<Match>

    // Obtém todos os sets que pertencem a partidas existentes
    @Query("SELECT * FROM match_sets WHERE matchId IN (SELECT id FROM matches)")
    suspend fun getAllSets(): List<MatchSet>
}

