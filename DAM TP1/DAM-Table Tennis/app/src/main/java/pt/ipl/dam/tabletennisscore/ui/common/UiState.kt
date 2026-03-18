package pt.ipl.dam.tabletennisscore.ui.common

// [Padrão de UI] Sealed class que representa os diferentes estados de um ecrã
sealed class UiState<out T> {
    object Loading : UiState<Nothing>() // O ecrã está a carregar dados (spinner)
    data class Success<T>(val data: T) : UiState<T>() // Os dados chegaram com sucesso
    data class Error(val message: String) : UiState<Nothing>() // Ocorreu um erro (exibir mensagem)
}

