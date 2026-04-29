package damA51388.core.di

import damA51388.core.network.DogApiService
import damA51388.core.repository.ImageRepository
import damA51388.core.storage.FavoritesManager
import damA51388.core.storage.ImageCache
import damA51388.core.network.NetworkMonitor
import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreModule = module {
    // Shared Preferences
    single { androidContext().getSharedPreferences("dog_prefs", Context.MODE_PRIVATE) }
    
    // Singletons
    single { DogApiService.instance }
    single { ImageCache(get()) }
    single { FavoritesManager(get()) }
    single { NetworkMonitor(androidContext()) }
    
    // Repository
    single { ImageRepository(get(), get()) }
}
