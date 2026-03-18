package pt.ipl.dam.tabletennisscore

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

//O Hilt é uma biblioteca de Injeção de Dependências (DI) criada pela Google especificamente para o Android.
// @HiltAndroidApp é o gatilho para a geração de código do Hilt.
// Esta anotação é obrigatória em qualquer app que use Hilt para Injeção de Dependências.
@HiltAndroidApp
class TableTennisApp : Application(), Configuration.Provider {

    // Injeção de Dependência no WorkManager.
    // Usamos o @Inject para que o Hilt forneça automaticamente a fábrica de Workers.
    @Inject lateinit var workerFactory: HiltWorkerFactory

    // Método chamado quando a App é lançada pela primeira vez.
    override fun onCreate() {
        super.onCreate()
        
        // Aplica Cores Dinâmicas em dispositivos Android 12+
        // As cores da App vão adaptar-se ao papel de parede do utilizador.
        DynamicColors.applyToActivitiesIfAvailable(this)
    }

    // Personalização do WorkManager para suportar o Hilt.
    // Isto permite que os nossos Workers (tarefas de fundo) recebam dependências injetadas.
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory) // Define a fábrica injetada pelo Hilt
            .build()
}