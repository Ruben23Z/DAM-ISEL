package pt.ipl.dam.tabletennisscore.data.repository

import kotlinx.coroutines.flow.Flow
import pt.ipl.dam.tabletennisscore.data.local.dao.ReminderDao
import pt.ipl.dam.tabletennisscore.data.local.entity.Reminder
import javax.inject.Inject
import javax.inject.Singleton

// [Repositório] Gere a persistência dos lembretes de treino
@Singleton
class ReminderRepository @Inject constructor(private val reminderDao: ReminderDao) {

    // Obtém todos os lembretes (reativo via Flow)
    fun getAllReminders(): Flow<List<Reminder>> = reminderDao.getAllReminders()

    // Lista apenas os lembretes ligados no momento
    suspend fun getEnabledReminders(): List<Reminder> = reminderDao.getEnabledReminders()

    // Insere um novo lembrete
    suspend fun insert(reminder: Reminder): Long = reminderDao.insert(reminder)

    // Atualiza um lembrete existente
    suspend fun update(reminder: Reminder) = reminderDao.update(reminder)

    // Apaga um lembrete
    suspend fun delete(reminder: Reminder) = reminderDao.delete(reminder)

    // Procura por ID
    suspend fun getById(id: Long): Reminder? = reminderDao.getById(id)
}

