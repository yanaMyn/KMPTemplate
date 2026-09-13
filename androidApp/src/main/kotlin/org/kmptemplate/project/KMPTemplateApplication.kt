package org.kmptemplate.project

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.kmptemplate.project.auth.LoginViewModel
import org.kmptemplate.project.auth.RegisterViewModel
import org.kmptemplate.project.di.initKoin
import org.kmptemplate.project.walkthrough.WalkthroughViewModel

/** ViewModel Android — thin wrappers agar store bertahan config change (lewat ViewModelStore). */
private val androidViewModelModule = module {
    viewModel { LoginViewModel(store = get()) }
    viewModel { RegisterViewModel(store = get()) }
    viewModel { WalkthroughViewModel(store = get()) }
}

class KMPTemplateApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger(Level.INFO)
            androidContext(this@KMPTemplateApplication)
            modules(androidViewModelModule)
        }
    }
}
