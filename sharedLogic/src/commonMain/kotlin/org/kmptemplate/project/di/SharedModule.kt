package org.kmptemplate.project.di

import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.kmptemplate.project.auth.data.AuthDataSource
import org.kmptemplate.project.auth.data.AuthRepository
import org.kmptemplate.project.auth.data.AuthRepositoryImpl
import org.kmptemplate.project.auth.data.RemoteAuthDataSource
import org.kmptemplate.project.auth.mvi.LoginStore
import org.kmptemplate.project.auth.mvi.RegisterStore
import org.kmptemplate.project.network.createHttpClient
import org.kmptemplate.project.walkthrough.data.RemoteWalkthroughDataSource
import org.kmptemplate.project.walkthrough.data.WalkthroughDataSource
import org.kmptemplate.project.walkthrough.data.WalkthroughRepository
import org.kmptemplate.project.walkthrough.data.WalkthroughRepositoryImpl
import org.kmptemplate.project.walkthrough.mvi.WalkthroughStore

/**
 * Modul DI shared untuk seluruh platform.
 *
 * - `HttpClient` = single (satu instance sepanjang hidup aplikasi — Ktor mahal untuk diciptakan).
 * - Repositories & DataSources = single (stateless dan aman dibagi).
 * - Stores = factory (setiap layar dapat instance segar; siklus hidup dikelola oleh ViewModel
 *   di sisi Android atau `@StateObject` di sisi iOS).
 */
val sharedModule = module {
    single<HttpClient> { createHttpClient() }

    single<AuthDataSource> { RemoteAuthDataSource(client = get()) }
    single<AuthRepository> { AuthRepositoryImpl(dataSource = get()) }

    single<WalkthroughDataSource> { RemoteWalkthroughDataSource(client = get()) }
    single<WalkthroughRepository> { WalkthroughRepositoryImpl(dataSource = get()) }

    // Stores mempertahankan `CoroutineScope` default (Main + SupervisorJob).
    // Owner (Android ViewModel `onCleared` / iOS `deinit`) wajib memanggil `store.close()`.
    factory { LoginStore(repository = get()) }
    factory { RegisterStore(repository = get()) }
    factory { WalkthroughStore(repository = get()) }
}

/** Entry point Koin — dipanggil dari Application (Android). */
fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(sharedModule)
}

/**
 * Entry point Koin no-arg untuk konsumen native (Swift).
 * Kotlin/Native tidak mengekspor default-lambda arg secara idiomatis ke Swift, jadi
 * kita sediakan pembungkus tanpa argumen.
 */
fun startSharedKoin() {
    initKoin { }
}
