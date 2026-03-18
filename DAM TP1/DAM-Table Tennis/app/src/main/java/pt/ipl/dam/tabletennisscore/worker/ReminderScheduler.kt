package pt.ipl.dam.tabletennisscore.worker

import android.content.Context
import androidx.work.*
import pt.ipl.dam.tabletennisscore.data.local.entity.Reminder
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

// @Singleton garante que existe apenas uma instância desta classe em toda a App
@Singleton
class ReminderScheduler @Inject constructor(private val context: Context) {

    // Função para agendar um lembrete no WorkManager
    fun schedule(reminder: Reminder) {
        val now = Calendar.getInstance() // Momento atual (agora)
        val target = Calendar.getInstance().apply {
            // Definimos a hora e minuto que o utilizador escolheu
            set(Calendar.HOUR_OF_DAY, reminder.scheduledHour)
            set(Calendar.MINUTE, reminder.scheduledMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // [Lógica] Se a hora escolhida já passou hoje, saltamos para o dia seguinte
        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        // [Lógica de Dias de Repetição]
        // O utilizador pode escolher vários dias (Seg, Ter, etc.).
        // A 'repeatDayMask' guarda isso num único número usando bits.
        var daysChecked = 0
        while (daysChecked < 7) {
            val calendarDay = target.get(Calendar.DAY_OF_WEEK)
            
            // Convertemos o formato do Calendar (Dom=1, Seg=2...) para o nosso índice (Seg=0...Dom=6)
            val maskIndex = when (calendarDay) {
                Calendar.MONDAY -> 0
                Calendar.TUESDAY -> 1
                Calendar.WEDNESDAY -> 2
                Calendar.THURSDAY -> 3
                Calendar.FRIDAY -> 4
                Calendar.SATURDAY -> 5
                Calendar.SUNDAY -> 6
                else -> 0
            }

            // Verificamos se o bit correspondente ao dia está "ligado" (1) na máscara
            if ((reminder.repeatDayMask and (1 shl maskIndex)) != 0) {
                break // Encontrámos o próximo dia em que o lembrete deve tocar
            }
            // Se o dia atual não estiver na lista de repetição, tentamos o dia seguinte
            target.add(Calendar.DAY_OF_YEAR, 1)
            daysChecked++
        }

        // Calculamos o atraso (delay) necessário para o WorkManager esperar até disparar
        val delay = target.timeInMillis - now.timeInMillis

        // Criamos os dados que o Worker vai precisar (Título, Notas, ID)
        val data = workDataOf(
            ReminderWorker.KEY_TITLE to reminder.title,
            ReminderWorker.KEY_NOTES to reminder.notes,
            ReminderWorker.KEY_REMINDER_ID to reminder.id
        )

        // Criamos um pedido de trabalho único (OneTimeWorkRequest) com o delay calculado
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS) // O WorkManager vai "dormir" até chegar a hora
            .setInputData(data) // Passamos os dados para a notificação
            .addTag("reminder_${reminder.id}") // Tag para podermos identificar este trabalho depois
            .build()

        // Enviamos o pedido para o WorkManager do sistema
        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "reminder_${reminder.id}", // ID único: garante que não agendamos o mesmo lembrete duas vezes
                ExistingWorkPolicy.REPLACE, // Se já houver um agendado com este ID, substitui pelo novo
                request
            )
    }

    // Função para cancelar um lembrete agendado através do seu ID único
    fun cancel(reminderId: Long) {
        WorkManager.getInstance(context).cancelUniqueWork("reminder_$reminderId")
    }
}