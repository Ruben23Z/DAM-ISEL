package pt.ipl.dam.tabletennisscore.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import pt.ipl.dam.tabletennisscore.data.local.dao.MatchDao
import pt.ipl.dam.tabletennisscore.data.local.dao.PlayerDao
import pt.ipl.dam.tabletennisscore.data.local.dao.ReminderDao
import pt.ipl.dam.tabletennisscore.data.local.entity.Match
import pt.ipl.dam.tabletennisscore.data.local.entity.MatchSet
import pt.ipl.dam.tabletennisscore.data.local.entity.Player
import pt.ipl.dam.tabletennisscore.data.local.entity.Reminder

// [Configuração Room] Classe principal que gere a base de dados local SQLite
@Database(
    // Listamos todas as entidades (tabelas) que fazem parte da base de dados
    entities = [Player::class, Match::class, MatchSet::class, Reminder::class],
    version = 1, // Versão inicial do esquema da base de dados
    exportSchema = true // Permite exportar o esquema para ficheiros JSON (bom para controlo de versão)
)
abstract class AppDatabase : RoomDatabase() {
    // Métodos abstratos que o Room irá implementar para nos dar acesso aos DAOs
    abstract fun playerDao(): PlayerDao
    abstract fun matchDao(): MatchDao
    abstract fun reminderDao(): ReminderDao
}