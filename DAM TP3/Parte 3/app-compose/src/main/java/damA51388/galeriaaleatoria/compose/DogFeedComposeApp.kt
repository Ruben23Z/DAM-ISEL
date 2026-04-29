package damA51388.galeriaaleatoria.compose

import android.app.Application
import damA51388.core.di.coreModule
import damA51388.galeriaaleatoria.compose.di.appComposeModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DogFeedComposeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@DogFeedComposeApp)
            modules(coreModule, appComposeModule)
        }
    }
}
