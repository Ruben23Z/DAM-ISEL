package pt.ipl.dam.tabletennisscore.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pt.ipl.dam.tabletennisscore.domain.model.OverallStats
import pt.ipl.dam.tabletennisscore.domain.usecase.GetStatsUseCase
import pt.ipl.dam.tabletennisscore.ui.common.UiState
import javax.inject.Inject

// [ViewModel] Gere o carregamento de dados estatísticos
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getStatsUseCase: GetStatsUseCase // Caso de Uso que processa as contas matemáticas
) : ViewModel() {

    // MutableStateFlow para gerir o estado de carregamento interno
    private val _stats = MutableStateFlow<UiState<OverallStats>>(UiState.Loading)
    val stats: StateFlow<UiState<OverallStats>> = _stats.asStateFlow()

    init { 
        loadStats() // Carrega logo ao abrir o fragmento
    }

    /** Dispara o cálculo de estatísticas duma forma assíncrona (IO) */
    fun loadStats() {
        viewModelScope.launch {
            _stats.value = UiState.Loading
            try {
                // Obtém os dados processados do UseCase
                _stats.value = UiState.Success(getStatsUseCase())
            } catch (e: Exception) {
                _stats.value = UiState.Error(e.message ?: "Erro ao carregar estatísticas")
            }
        }
    }
}