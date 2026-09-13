# KMP Architecture Guardrails

## 1. Project Overview & Tech Stack

* **Language:** Kotlin Multiplatform (Kotlin 2.2.0 — dikunci oleh SKIE), Swift 5.9+.
* **UI:** Jetpack Compose (Android) + SwiftUI (iOS) — Native UI per platform.
* **Architecture:** MVI Store Pattern dengan `StateFlow`.
* **DI:** Koin 4.x — dipanggil di `KMPTemplateApplication.onCreate()` (Android) dan `iOSApp.init()` (iOS via `SharedModuleKt.startSharedKoin()`).
* **Networking:** Ktor 3.3.3 + `kotlinx.serialization` (base URL: `https://jsonplaceholder.typicode.com`).
* **Swift interop:** SKIE (sealed class → Swift enum, `Flow` → `AsyncSequence`, `suspend` → `async`).
* **Modules:**
  * `:sharedLogic` — satu-satunya KMP module. Berisi models, MVI stores, repositories, Ktor data sources, DI, framework `SharedLogic.framework` untuk iOS.
  * `:androidApp` — aplikasi Android. Berisi Compose UI (screens, route composables, komponen), ViewModel wrapper, `KMPTemplateApplication`, previews.
  * `iosApp/` — Xcode project. Berisi SwiftUI views, Swift `@MainActor` view models.

---

## 2. Core Architectural Rules

### 2.1 Shared Logic Purity
- `sharedLogic/src/commonMain` **DILARANG** meng-import `android.*`, `androidx.*`, `UIKit.*`, `SwiftUI.*`.
- DTO **HARUS** `internal`.
- File di package `.mvi.*` **DILARANG** meng-import Ktor (`io.ktor.*`) atau `.data.dto.*`. Interaksi network **HARUS** lewat Repository → DataSource.

### 2.2 MVI Store Pattern

**State:** immutable `data class` dengan properti `val` only.

**Intent:** `sealed class` (bukan sealed interface — konsisten dengan basis kode) supaya `when` di `dispatch` bisa exhaustive.

**Store:**
```kotlin
class LoginStore(
    private val repository: AuthRepository,                                          // required
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun dispatch(intent: LoginIntent) { /* when block */ }

    /** Wajib dipanggil owner (ViewModel `onCleared` / `deinit`) untuk menutup scope. */
    fun close() { scope.cancel() }
}
```

- **Constructor default** untuk `repository` / `dataSource` / `HttpClient` **DILARANG**. Konstruksi hanya lewat Koin (production) atau injection eksplisit (test/preview). Ini melarang bocor `HttpClient` per screen.
- `scope` **BOLEH** punya default (`Dispatchers.Main + SupervisorJob`) — production sensible default; test override via `UnconfinedTestDispatcher`.

### 2.3 Lifecycle Ownership

- **Android:** Setiap fitur punya `FooViewModel(val store: FooStore) : androidx.lifecycle.ViewModel()` yang override `onCleared()` untuk memanggil `store.close()`. Compose memakai `koinViewModel<FooViewModel>()` — store bertahan config change.
- **iOS:** Setiap fitur punya Swift `@MainActor final class FooViewModel: ObservableObject` yang memegang store dan memanggil `store.close()` di `deinit`. Store diperoleh via `KoinHelpersKt.getFooStore()`.

### 2.4 Anti Memory Leak
- Setiap store yang punya `CoroutineScope` **WAJIB** ditutup oleh owner. `.close()` = single point of cleanup.
- Compose Route composable **DILARANG** memanggil `store.close()` sendiri — biarkan ViewModel yang mengelola.

### 2.5 Native UI Isolation
- Android view **HANYA** berinteraksi dengan store via `.state.collectAsState()` + `store.dispatch(...)`.
- iOS view **HANYA** berinteraksi dengan store via `for await state in store.state` + `store.dispatch(intent:)`.
- Aturan UI komponen: lihat `ui-component-guidelines.md`.

### 2.6 iOS UI/UX
- Semua SwiftUI **WAJIB** mengikuti Apple Human Interface Guidelines. Lihat `apple-hig-guidelines.md`.

---

## 3. DI (Koin) Rules

- Semua wiring shared **HARUS** ada di `sharedLogic/.../di/SharedModule.kt`. Jangan buat `single { }` untuk domain object di module Android saja.
- Android-only ViewModel di-declare di `androidApp/.../KMPTemplateApplication.kt` dalam `androidViewModelModule`.
- **HttpClient adalah `single`** — satu instance seumur hidup proses. Jangan buat `createHttpClient()` di tempat lain.
- **Stores adalah `factory`** — fresh instance per screen, di-manage oleh ViewModel/`@StateObject`.
- **Repositories & DataSources adalah `single`** — stateless dan aman dibagi.

Untuk konsumen Swift, ekspos helper non-generic di `sharedLogic/.../di/KoinHelpers.kt` (`getLoginStore()`, dst.) karena Swift tidak bisa memanggil `Koin.get<T>()` reified generic.

---

## 4. Konsist (Architecture Tests)

Test arsitektur ada di `sharedLogic/src/androidHostTest/kotlin/.../architecture/` dan **dijalankan sebagai bagian dari `./gradlew :sharedLogic:build`**. Aturan yang ditegakkan otomatis:

| File | Aturan |
| :--- | :--- |
| `FeatureBoundaryTest` | `auth.*` ⊥ `walkthrough.*` (baik di sharedLogic maupun androidApp) |
| `MviPurityTest` | `mvi/` tidak import Ktor / DTO; State = data class; Intent = sealed; State props = val |
| `DataLayerTest` | DTO = internal; RepositoryImpl & DataSource impl ada di `.data.` package |

Tambahkan `@Test` baru di `architecture/` jika perlu aturan baru. Pakai `sharedLogicScope()` untuk aturan module-local, `productionScope()` untuk aturan lintas-module.

---

## 5. Rules of Engagement

- **Rencana Dulu, Baru Eksekusi:** Untuk fitur baru atau refactor > 2 file, buat dokumen rencana MVI dan tunggu approval.
- **Definisi Selesai:** Task **BELUM SELESAI** sampai `./gradlew :sharedLogic:build` mengembalikan `BUILD SUCCESSFUL` (mencakup 30 unit test + 42 Konsist assertion).
- **Jangan Hapus Kode Existing** tanpa alasan dokumentasi.
- **Jangan Tambah Dependency** (Gradle / Swift Package) tanpa persetujuan eksplisit.
