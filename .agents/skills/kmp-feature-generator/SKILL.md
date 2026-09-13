---
name: kmp-feature-generator
description: >-
  Menulis kode fitur baru pada proyek KMP dengan pola MVI + StateFlow, Koin DI, Ktor networking, dan Native UI (Compose Android / SwiftUI iOS).
---

# Agent Role: KMP Code Generator & Executor

## 1. Prasyarat Baca

Sebelum menulis kode, agent WAJIB baca:
1. `.agents/rules/agent-safety-guardrails.md` — blacklist file & command.
2. `.agents/rules/kmp-architecture-guardrails.md` — MVI, DI, lifecycle.
3. `.agents/rules/ui-component-guidelines.md` — Route/Screen split, tokens, komponen.
4. `.agents/rules/apple-hig-guidelines.md` — untuk pekerjaan iOS.

## 2. Alur Kerja Membuat Fitur Baru `foo`

### Step 1 — Shared Logic (`sharedLogic/src/commonMain/kotlin/org/kmptemplate/project/foo/`)

Buat struktur:
```
foo/
├── model/FooItem.kt                    # data class
├── data/
│   ├── FooDataSource.kt                # interface + RemoteFooDataSource
│   ├── FooRepository.kt                # interface + FooRepositoryImpl
│   └── dto/FooDto.kt                   # internal @Serializable
└── mvi/FooMVI.kt                       # State, Intent (sealed), Store
```

**Store template** (patuhi StateFlow, wajib `close()`, tanpa default `repository`):
```kotlin
package org.kmptemplate.project.foo.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.kmptemplate.project.foo.data.FooRepository

data class FooState(
    val items: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed class FooIntent {
    data object LoadData : FooIntent()
    data class Submit(val text: String) : FooIntent()
}

class FooStore(
    private val repository: FooRepository,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) {
    private val _state = MutableStateFlow(FooState(isLoading = true))
    val state: StateFlow<FooState> = _state.asStateFlow()

    private var loadJob: Job? = null

    init { dispatch(FooIntent.LoadData) }

    fun dispatch(intent: FooIntent) {
        when (intent) {
            is FooIntent.LoadData -> handleLoad()
            is FooIntent.Submit -> handleSubmit(intent.text)
        }
    }

    fun close() { scope.cancel() }

    private fun handleLoad() {
        loadJob?.cancel()
        loadJob = scope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { repository.fetchItems() }.fold(
                onSuccess = { items -> _state.update { it.copy(isLoading = false, items = items) } },
                onFailure = { err -> _state.update { it.copy(isLoading = false, errorMessage = err.message) } }
            )
        }
    }

    private fun handleSubmit(text: String) {
        _state.update { it.copy(items = it.items + text) }
    }
}
```

### Step 2 — Koin wiring

Edit `sharedLogic/.../di/SharedModule.kt`:
```kotlin
val sharedModule = module {
    // ...existing bindings
    single<FooDataSource> { RemoteFooDataSource(client = get()) }
    single<FooRepository> { FooRepositoryImpl(dataSource = get()) }
    factory { FooStore(repository = get()) }
}
```

Edit `sharedLogic/.../di/KoinHelpers.kt` untuk expose Swift-friendly resolver:
```kotlin
fun getFooStore(): FooStore = KoinPlatform.getKoin().get()
```

### Step 3 — Android ViewModel & Route (`androidApp/.../foo/`)

`FooViewModel.kt`:
```kotlin
class FooViewModel(val store: FooStore) : androidx.lifecycle.ViewModel() {
    override fun onCleared() { store.close() }
}
```

Register di `KMPTemplateApplication.androidViewModelModule`:
```kotlin
viewModel { FooViewModel(store = get()) }
```

`FooRoute.kt` (STATEFUL):
```kotlin
@Composable
fun FooRoute(
    viewModel: FooViewModel = koinViewModel(),
    onBack: () -> Unit = {},
) {
    FooScreen(store = viewModel.store, onBack = onBack)
}
```

`FooScreen.kt` (STATELESS — hanya menerima store):
```kotlin
@Composable
fun FooScreen(
    store: FooStore,
    onBack: () -> Unit = {},
) {
    val state by store.state.collectAsState()
    // ...pakai komponen dari `ui/components/`, token dari `ui/theme/AppTheme.kt`
}
```

Preview scaffolding wajib ada di `FooRoute.kt` (bukan Screen), memakai `previewFooStore()` dengan fake data source & `Dispatchers.Unconfined`.

### Step 4 — iOS View (`iosApp/iosApp/Foo/`)

`FooViewModel.swift`:
```swift
@MainActor
final class FooViewModel: ObservableObject {
    @Published var state: FooState
    private let store: FooStore
    private var stateTask: Task<Void, Never>?

    init(store: FooStore = KoinHelpersKt.getFooStore()) {
        self.store = store
        self.state = store.state.value
        self.stateTask = Task { [weak self] in
            guard let self else { return }
            for await newState in self.store.state { self.state = newState }
        }
    }
    deinit {
        stateTask?.cancel()
        store.close()
    }
    func onSubmit() { store.dispatch(intent: FooIntent.Submit(text: "...")) }
}
```

`FooView.swift` — pakai komponen dari `iosApp/iosApp/UI/`, ikuti `apple-hig-guidelines.md`.

### Step 5 — Verifikasi

```bash
./gradlew :sharedLogic:build          # tests + Konsist arch tests
./gradlew :androidApp:assembleDebug
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -sdk iphonesimulator \
  -destination 'generic/platform=iOS Simulator' build
```

## 3. Aturan Wajib

1. **Store constructor tidak boleh punya default** untuk `repository`/`dataSource`/`client` — Koin wajib.
2. **`store.close()` wajib** dipanggil dari `ViewModel.onCleared()` (Android) & `deinit` (iOS).
3. **Route ↔ Screen split wajib** — lihat `ui-component-guidelines.md` §1.1.
4. **UI tidak boleh duplikat** — lihat `ui-component-guidelines.md` §1.4. Sebelum menulis UI di Screen/View baru:
   - Cek `androidApp/src/main/kotlin/org/kmptemplate/project/ui/components/` (Android).
   - Cek `iosApp/iosApp/UI/Components/` (iOS).
   - Jika pola sudah ada → **wajib pakai** komponen tersebut.
   - Jika belum ada tapi akan berulang di ≥2 layar → **buat komponen dulu** di `ui/components/`, baru pakai di Screen.
   - Grep cepat untuk memastikan tidak menyisakan primitif mentah di Screen:
     ```bash
     grep -n "OutlinedTextField\|SecureField\|TextField(" \
       androidApp/src/main/kotlin/**/*Screen.kt iosApp/iosApp/**/*View.swift
     ```
5. **Design tokens wajib** — tidak boleh hardcode `.dp`/`.sp`/`CGFloat` di luar `ui/theme/`.
6. **Konsist wajib hijau** — cek `FeatureBoundaryTest`, `MviPurityTest`, `DataLayerTest`.

## 4. Blacklist File (Jangan Sentuh)

Lihat `agent-safety-guardrails.md`. Ringkasan: `build.gradle.kts` (root + submodule), `settings.gradle.kts`, `iosApp.xcodeproj/*` internal, `gradle.properties`, `local.properties`, `private/`, `private.txt`.
