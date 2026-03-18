package pt.ipl.dam.tabletennisscore.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

// [Entidade] Representa um Lembrete de Treino na base de dados
@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // ID do lembrete
    val title: String, // Título (Ex: "Treinar Smash!")
    val notes: String = "", // Notas adicionais para a notificação
    val scheduledHour: Int,   // Hora agendada (0-23)
    val scheduledMinute: Int, // Minuto agendado (0-59)
    // [Máscara de Bits] Guarda os dias da semana selecionados (ex: 0b1111111 = todos os dias)
    val repeatDayMask: Int = 0b1111111, 
    val isEnabled: Boolean = true // Se o lembrete está ativo ou desligado
)
