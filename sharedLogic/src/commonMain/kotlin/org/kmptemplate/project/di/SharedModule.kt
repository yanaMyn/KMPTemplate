package org.kmptemplate.project.di

import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.kmptemplate.project.auth.data.AuthDataSource
import org.kmptemplate.project.auth.data.AuthRepository
import org.kmptemplate.project.auth.data.AuthRepositoryImpl
import org.kmptemplate.project.auth.data.MockAuthDataSource
import org.kmptemplate.project.auth.data.RemoteAuthDataSource
import org.kmptemplate.project.auth.mvi.LoginStore
import org.kmptemplate.project.auth.mvi.RegisterStore
import org.kmptemplate.project.network.createHttpClient
import org.kmptemplate.project.walkthrough.data.MockWalkthroughDataSource
import org.kmptemplate.project.walkthrough.data.RemoteWalkthroughDataSource
import org.kmptemplate.project.walkthrough.data.WalkthroughDataSource
import org.kmptemplate.project.walkthrough.data.WalkthroughRepository
import org.kmptemplate.project.walkthrough.data.WalkthroughRepositoryImpl
import org.kmptemplate.project.walkthrough.mvi.WalkthroughStore

// ---------------------------------------------------------------------------
// Mock DataSource toggles (per fitur).
//
// Flip ke `true` untuk memakai `Mock<Feature>DataSource` (data hardcoded, tanpa
// network). Flip balik ke `false` untuk memakai `Remote<Feature>DataSource`
// (Ktor + jsonplaceholder).
//
// Setiap flag independen — dev bebas mix & match (mis. auth pakai API real,
// walkthrough pakai mock). Perlu rebuild + relaunch setelah flip.
//
// ⚠️ Jangan commit dalam kondisi `true` ke branch utama.
// ---------------------------------------------------------------------------

const val isAuthApiMocked: Boolean = false
const val isWalkthroughApiMocked: Boolean = false

/**
 * Modul DI shared untuk seluruh platform.
 *
 * ## Ringkasan lifetime
 * - `HttpClient` = single (satu instance sepanjang hidup aplikasi).
 * - Repositories & DataSources = single (stateless dan aman dibagi).
 * - Stores = factory (setiap layar dapat instance segar; siklus hidup dikelola
 *   oleh Android ViewModel `onCleared` atau iOS `@StateObject` `deinit`).
 *
 * ## Mock switch per fitur
 * Setiap `DataSource` mengecek flag `is<Feature>ApiMocked` di atas.
 * `HttpClient` tetap dibangun (murah, satu instance) sehingga fitur yang belum
 * di-mock tetap bisa hit backend nyata bersamaan. Toggle real ↔ mock hanya
 * mengubah baris `const val` — tidak menyentuh Repository / Store / UI.
 */
val sharedModule = module {
    single<HttpClient> { createHttpClient() }

    single<AuthDataSource> {
        if (isAuthApiMocked) MockAuthDataSource()
        else RemoteAuthDataSource(client = get())
    }
    single<AuthRepository> { AuthRepositoryImpl(dataSource = get()) }

    single<WalkthroughDataSource> {
        if (isWalkthroughApiMocked) MockWalkthroughDataSource()
        else RemoteWalkthroughDataSource(client = get())
    }
    single<WalkthroughRepository> { WalkthroughRepositoryImpl(dataSource = get()) }

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
