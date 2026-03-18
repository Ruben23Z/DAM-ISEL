package pt.ipl.dam.tabletennisscore.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pt.ipl.dam.tabletennisscore.worker.ReminderScheduler
import javax.inject.Singleton

//Fornece dependências de âmbito geral da aplicação
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Define como o Hilt deve criar o ReminderScheduler.
    // Usamos @Singleton para que exista apenas um agendador em toda a app.
    @Provides
    @Singleton
    fun provideReminderScheduler(@ApplicationContext context: Context): ReminderScheduler =
        ReminderScheduler(context)
}

