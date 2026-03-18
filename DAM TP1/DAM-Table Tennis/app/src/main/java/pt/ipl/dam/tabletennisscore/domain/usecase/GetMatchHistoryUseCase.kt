package pt.ipl.dam.tabletennisscore.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pt.ipl.dam.tabletennisscore.data.local.relation.MatchWithSets
import pt.ipl.dam.tabletennisscore.data.repository.MatchRepository
import pt.ipl.dam.tabletennisscore.domain.model.MatchSummary
import pt.ipl.dam.tabletennisscore.domain.model.SetDetail
import javax.inject.Inject

// [Caso de Uso] Obtém o histórico de partidas formatado para a UI
class GetMatchHistoryUseCase @Inject constructor(private val repository: MatchRepository) {

    // Invoca o repositório e mapeia os dados brutos da DB para o modelo de domínio MatchSummary
    operator fun invoke(): Flow<List<MatchSummary>> =
        repository.getAllMatchesWithSets().map { list -> list.map { it.toSummary() } }

    /** Extensão privada para converter a relação da DB (MatchWithSets) num resumo legível (MatchSummary) */
    private fun MatchWithSets.toSummary(): MatchSummary {
        val sets = sets.sortedBy { it.setNumber } // Garante a ordem dos sets
        val duration = (match.finishedAt - match.startedAt) / 60_000 // Converte milissegundos em minutos
        val sets1 = sets.count { it.winnerName == match.player1Name } // Contagem de sets ganhos pelo J1
        val sets2 = sets.count { it.winnerName == match.player2Name } // Contagem de sets ganhos pelo J2
        
        return MatchSummary(
            id = match.id,
            player1Name = match.player1Name,
            player2Name = match.player2Name,
            winnerName = match.winnerName,
            setsScore = "$sets1-$sets2", // Formata o placar final (ex: "3-0")
            durationMinutes = duration,
            startedAt = match.startedAt,
            bestOf = match.bestOf,
            pointsPerSet = match.pointsPerSet,
            // Converte os sets da DB em detalhes de set para a UI
            setDetails = sets.map { SetDetail(it.setNumber, it.score1, it.score2, it.winnerName) }
        )
    }
}

