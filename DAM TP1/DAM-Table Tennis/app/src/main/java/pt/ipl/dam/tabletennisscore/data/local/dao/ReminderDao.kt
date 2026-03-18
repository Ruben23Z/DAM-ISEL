package pt.ipl.dam.tabletennisscore.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pt.ipl.dam.tabletennisscore.data.local.entity.Reminder

// [DAO] Interface para gerir os Lembretes de treino
@Dao
interface ReminderDao {
    // Lista todos os lembretes ordenados por ordem cronológica (hora e minuto)
    @Query("SELECT * FROM reminders ORDER BY scheduledHour ASC, scheduledMinute ASC")
    fun getAllReminders(): Flow<List<Reminder>>

    // Retorna apenas os lembretes que estão ativos (isEnabled = 1)
    @Query("SELECT * FROM reminders WHERE isEnabled = 1")
    suspend fun getEnabledReminders(): List<Reminder>

    // Insere ou substitui um lembrete (útil para edição rápida)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: Reminder): Long

    // Atualiza o estado (ativo/inativo) ou hora do lembrete
    @Update
    suspend fun update(reminder: Reminder)

    // Remove um lembrete permanentemente
    @Delete
    suspend fun delete(reminder: Reminder)

    // Procura um lembrete específico pelo ID
    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getById(id: Long): Reminder?
}

