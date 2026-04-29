package damA51388.galeriaaleatoria.di

import damA51388.galeriaaleatoria.viewmodel.ImageViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appXmlModule = module {
    viewModel { ImageViewModel(get()) }
}
