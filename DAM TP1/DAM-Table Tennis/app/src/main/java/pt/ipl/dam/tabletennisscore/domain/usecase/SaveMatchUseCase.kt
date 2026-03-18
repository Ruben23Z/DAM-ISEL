package pt.ipl.dam.tabletennisscore.domain.usecase

import pt.ipl.dam.tabletennisscore.data.local.entity.Match
import pt.ipl.dam.tabletennisscore.data.local.entity.MatchSet
import pt.ipl.dam.tabletennisscore.data.repository.MatchRepository
import javax.inject.Inject

// [Caso de Uso] Centraliza a lógica de guardar uma partida finalizada
class SaveMatchUseCase @Inject constructor(private val repository: MatchRepository) {
    // Invoca o repositório para guardar a partida e os seus sets de forma atómica
    suspend operator fun invoke(match: Match, sets: List<MatchSet>): Long =
        repository.saveMatch(match, sets)
}

