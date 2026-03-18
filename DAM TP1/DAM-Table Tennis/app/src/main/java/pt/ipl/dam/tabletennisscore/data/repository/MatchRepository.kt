package pt.ipl.dam.tabletennisscore.data.repository

import kotlinx.coroutines.flow.Flow
import pt.ipl.dam.tabletennisscore.data.local.dao.MatchDao
import pt.ipl.dam.tabletennisscore.data.local.entity.Match
import pt.ipl.dam.tabletennisscore.data.local.entity.MatchSet
import pt.ipl.dam.tabletennisscore.data.local.relation.MatchWithSets
import javax.inject.Inject
import javax.inject.Singleton

// [Repositório] Centraliza o acesso aos dados das partidas, abstraindo o DAO para o resto da App
@Singleton
class MatchRepository @Inject constructor(private val matchDao: MatchDao) {

    // Retorna o fluxo contínuo de partidas (reativo)
    fun getAllMatchesWithSets(): Flow<List<MatchWithSets>> = matchDao.getAllMatchesWithSets()

    // Obtém uma partida específica de forma assíncrona
    suspend fun getMatchWithSets(matchId: Long): MatchWithSets? = matchDao.getMatchWithSets(matchId)

    // [Lógica] Guarda uma partida e todos os seus sets numa única operação.
    // O copy() é usado para garantir que cada set tem o ID correto da partida recém-criada.
    suspend fun saveMatch(match: Match, sets: List<MatchSet>): Long {
        val matchId = matchDao.insertMatch(match)
        sets.forEach { matchDao.insertSet(it.copy(matchId = matchId)) }
        return matchId
    }

    // Remove uma partida
    suspend fun deleteMatch(matchId: Long) = matchDao.deleteMatch(matchId)

    // Lista estática de partidas (sem Flow)
    suspend fun getAllMatchesList(): List<Match> = matchDao.getAllMatchesList()

    // Lista de todos os sets individuais
    suspend fun getAllSets(): List<MatchSet> = matchDao.getAllSets()
}

