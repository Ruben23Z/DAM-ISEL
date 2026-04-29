package damA51388.galeriaaleatoria

import android.app.Application
import damA51388.core.di.coreModule
import damA51388.galeriaaleatoria.di.appXmlModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DogFeedXmlApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@DogFeedXmlApp)
            modules(coreModule, appXmlModule)
        }
    }
}
