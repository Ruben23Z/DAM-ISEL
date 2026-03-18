package pt.ipl.dam.tabletennisscore.ui.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pt.ipl.dam.tabletennisscore.data.local.entity.Reminder
import pt.ipl.dam.tabletennisscore.data.repository.ReminderRepository
import pt.ipl.dam.tabletennisscore.ui.common.UiState
import pt.ipl.dam.tabletennisscore.worker.ReminderScheduler
import javax.inject.Inject

// [ViewModel] Gere o ciclo de vida dos lembretes e a sua integração com o WorkManager
@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val scheduler: ReminderScheduler // Injetado para agendar tarefas em background
) : ViewModel() {

    // Lista de lembretes convertida num estado de UI observável
    private val _reminders = reminderRepository.getAllReminders()
        .map<List<Reminder>, UiState<List<Reminder>>> { UiState.Success(it) }
        .catch { emit(UiState.Error(it.message ?: "Erro ao carregar lembretes")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)
    
    val reminders: StateFlow<UiState<List<Reminder>>> = _reminders

    /** Procura um lembrete específico pelo ID (usado no ecrã de edição) */
    fun getReminder(id: Long): Flow<Reminder?> {
        return reminderRepository.getAllReminders().map { list ->
            list.find { it.id == id }
        }
    }

    /** Cria um novo lembrete e agenda-o imediatamente no sistema */
    fun saveReminder(reminder: Reminder) {
        viewModelScope.launch {
            val id = reminderRepository.insert(reminder)
            val saved = reminder.copy(id = id)
            // Se o lembrete estiver ativo, manda para o WorkManager (scheduler)
            if (saved.isEnabled) scheduler.schedule(saved)
        }
    }

    /** Atualiza um lembrete existente e recalcula o agendamento */
    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch {
            reminderRepository.update(reminder)
            // Primeiro cancela o agendamento antigo para evitar duplicados
            scheduler.cancel(reminder.id)
            // Se continuar ativo, agenda com a nova hora/dias
            if (reminder.isEnabled) scheduler.schedule(reminder)
        }
    }

    /** Remove um lembrete e cancela qualquer tarefa pendente no sistema */
    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            reminderRepository.delete(reminder)
            scheduler.cancel(reminder.id)
        }
    }

    /** Atalho para ligar/desligar um lembrete rapidamente na lista */
    fun toggleEnabled(reminder: Reminder) {
        updateReminder(reminder.copy(isEnabled = !reminder.isEnabled))
    }
}

