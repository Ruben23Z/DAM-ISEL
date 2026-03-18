package pt.ipl.dam.tabletennisscore.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pt.ipl.dam.tabletennisscore.data.repository.MatchRepository
import pt.ipl.dam.tabletennisscore.domain.model.MatchSummary
import pt.ipl.dam.tabletennisscore.domain.usecase.GetMatchHistoryUseCase
import pt.ipl.dam.tabletennisscore.ui.common.UiState
import javax.inject.Inject

// [ViewModel] Gere os dados para o ecrã de histórico
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getMatchHistoryUseCase: GetMatchHistoryUseCase,
    private val matchRepository: MatchRepository
) : ViewModel() {

    // [Reatividade] Transforma o Flow da DB num Flow de UiState (Loading -> Success/Error)
    // stateIn: Converte o fluxo frio num fluxo quente que sobrevive a rotações de ecrã
    val matches: StateFlow<UiState<List<MatchSummary>>> = getMatchHistoryUseCase()
        .map<List<MatchSummary>, UiState<List<MatchSummary>>> { UiState.Success(it) }
        .catch { emit(UiState.Error(it.message ?: "Erro desconhecido")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000), // Mantém ativo por 5s após fechar UI
            initialValue = UiState.Loading
        )

    /** Apaga uma partida permanentemente através duma corrotina */
    fun deleteMatch(matchId: Long) {
        viewModelScope.launch { matchRepository.deleteMatch(matchId) }
    }
}

