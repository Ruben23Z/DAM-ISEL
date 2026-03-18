package pt.ipl.dam.tabletennisscore.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ipl.dam.tabletennisscore.data.repository.ReminderRepository
import pt.ipl.dam.tabletennisscore.worker.ReminderScheduler
import javax.inject.Inject

/**
 * [Receiver] Ouvinte de sistema que deteta quando o dispositivo termina de arrancar (Boot).
 * É crucial para garantir que os lembretes não se perdem após o telemóvel ser desligado.
 */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    // Repositório para aceder aos lembretes guardados na base de dados (Room)
    @Inject lateinit var reminderRepository: ReminderRepository
    
    // Agendador que comunica com o WorkManager
    @Inject lateinit var scheduler: ReminderScheduler

    /**
     * Método chamado pelo Android quando ocorre um evento do sistema (neste caso, o Boot).
     */
    override fun onReceive(context: Context, intent: Intent) {
        // Verifica se a intenção recebida é exatamente "O arranque foi concluído"
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            
            // Como a operação de leitura da DB é assíncrona, usamos uma Corrotina no Scope de IO
            CoroutineScope(Dispatchers.IO).launch {
                // Procura todos os lembretes que estavam ativos (isEnabled = true) 
                // e reagenda-os no WorkManager através do Scheduler.
                reminderRepository.getEnabledReminders().forEach { 
                    scheduler.schedule(it) 
                }
            }
        }
    }
}

