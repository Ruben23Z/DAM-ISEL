package pt.ipl.dam.tabletennisscore.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pt.ipl.dam.tabletennisscore.data.local.dao.MatchDao
import pt.ipl.dam.tabletennisscore.data.local.dao.PlayerDao
import pt.ipl.dam.tabletennisscore.data.local.dao.ReminderDao
import pt.ipl.dam.tabletennisscore.data.local.db.AppDatabase
import javax.inject.Singleton

//Fornece todas as dependências relacionadas com a Base de Dados Room
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // Fornece a instância única da base de dados local.
    // .fallbackToDestructiveMigration() apaga os dados se mudarmos a versão sem migração (útil em dev).
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "table_tennis_score.db")
            .fallbackToDestructiveMigration()
            .build()

    // Fornece o DAO de Jogadores a partir da base de dados injetada acima
    @Provides fun providePlayerDao(db: AppDatabase): PlayerDao = db.playerDao()
    
    // Fornece o DAO de Partidas
    @Provides fun provideMatchDao(db: AppDatabase): MatchDao = db.matchDao()
    
    // Fornece o DAO de Lembretes
    @Provides fun provideReminderDao(db: AppDatabase): ReminderDao = db.reminderDao()
}

