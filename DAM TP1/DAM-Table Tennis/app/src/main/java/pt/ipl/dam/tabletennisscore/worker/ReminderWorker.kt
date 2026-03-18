package pt.ipl.dam.tabletennisscore.worker

// Importações necessárias para notificações, contexto, Hilt e WorkManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import pt.ipl.dam.tabletennisscore.R
import pt.ipl.dam.tabletennisscore.data.repository.ReminderRepository


// @HiltWorker permite que este Worker receba dependências via Hilt (Injeção de Dependências)
@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context, // O contexto da aplicação (assistido pelo WorkManager)
    @Assisted params: WorkerParameters, // Parâmetros de execução (assistido pelo WorkManager)
    private val repository: ReminderRepository, // Repositório para aceder aos dados dos lembretes
    private val scheduler: ReminderScheduler // Agendador para programar a próxima ocorrência
) : CoroutineWorker(context, params) { // CoroutineWorker permite usar 'suspend' para tarefas assíncronas

    // doWork() é o método principal que executa a tarefa em background
    override suspend fun doWork(): Result {
        // Recuperamos os dados passados para o Worker através do inputData
        val title = inputData.getString(KEY_TITLE) ?: "Training Reminder" // Título da notificação
        val notes = inputData.getString(KEY_NOTES) ?: "" // Notas/descrição da notificação
        val reminderId = inputData.getLong(KEY_REMINDER_ID, 0L) // ID único do lembrete

        // Chamamos a função para criar e mostrar a notificação ao utilizador
        showNotification(title, notes, reminderId)

        // Lógica para Reagendar: Procuramos o lembrete na base de dados pelo ID
        val reminder = repository.getAllReminders().firstOrNull()?.find { it.id == reminderId }
        
        // Se o lembrete ainda existir e estiver ativo (enabled), agendamos a próxima repetição
        if (reminder != null && reminder.isEnabled) {
            scheduler.schedule(reminder)
        }

        // Indicamos ao WorkManager que a tarefa foi concluída com sucesso
        return Result.success()
    }

    // Função privada dedicada à construção e disparo da notificação visual
    private fun showNotification(title: String, notes: String, reminderId: Long) {
        // Obtemos o serviço de gestão de notificações do sistema Android
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // [Obrigatório desde Android 8.0] Criar o canal de notificação
        val channel = NotificationChannel(CHANNEL_ID, "Training Reminders", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "Table Tennis training session reminders" // Descrição do canal nas definições do sistema
        }
        // Registamos o canal no sistema
        notificationManager.createNotificationChannel(channel)

        // Construímos a notificação usando o Builder do NotificationCompat para compatibilidade
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Ícone que aparece na barra de estado
            .setContentTitle("🏓 $title") // Título da notificação com emoji
            .setContentText(notes.ifEmpty { "Time for your training session!" }) // Texto principal
            // BigTextStyle permite que o texto se expanda para mostrar notas longas
            .setStyle(NotificationCompat.BigTextStyle().bigText(notes.ifEmpty { "Time for your training session!" }))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Define a importância visual
            .setAutoCancel(true) // A notificação desaparece quando o utilizador toca nela
            .build()

        // Disparamos a notificação. Usamos o reminderId como ID da notificação para não as sobrepor
        notificationManager.notify(reminderId.toInt(), notification)
    }

    // Objeto companheiro para constantes usadas no agendamento e leitura de dados
    companion object {
        const val CHANNEL_ID = "training_reminders" // ID único do canal
        const val KEY_TITLE = "key_title" // Chave para o título no inputData
        const val KEY_NOTES = "key_notes" // Chave para as notas no inputData
        const val KEY_REMINDER_ID = "key_reminder_id" // Chave para o ID no inputData
    }
}

