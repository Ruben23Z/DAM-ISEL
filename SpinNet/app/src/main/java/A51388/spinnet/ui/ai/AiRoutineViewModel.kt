package A51388.spinnet.ui.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import A51388.spinnet.data.model.GeneratedRoutine
import A51388.spinnet.data.remote.GroqApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AiState {
    object Idle : AiState()
    object Loading : AiState()
    data class Success(val routine: GeneratedRoutine) : AiState()
    data class Error(val message: String) : AiState()
}

class AiRoutineViewModel : ViewModel() {
    private val _state = MutableStateFlow<AiState>(AiState.Idle)
    val state: StateFlow<AiState> = _state

    fun generateRoutine(prompt: String) {
        viewModelScope.launch {
            _state.value = AiState.Loading
            val result = GroqApiService.generateRoutine(prompt)
            _state.value = result.fold<AiState, GeneratedRoutine>(
                onSuccess = { AiState.Success(it) },
                onFailure = { AiState.Error(it.message ?: "Erro desconhecido") }
            )
        }
    }

    fun reset() { _state.value = AiState.Idle }
}
