package damA51388.galeriaaleatoria.compose.di

import damA51388.galeriaaleatoria.compose.viewmodel.ImageViewModelCompose
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appComposeModule = module {
    viewModel { ImageViewModelCompose(get(), get()) }
}
