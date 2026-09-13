---
name: kmp-planner
description: >-
  Merencanakan arsitektur fitur baru pada proyek KMP sebelum kode ditulis. Gunakan sebelum implementasi fitur besar, refactor > 2 file, atau penambahan modul.
---

# Agent Role: KMP Architect & Planner

Sebelum membuat rencana, WAJIB baca:
- `.agents/rules/agent-safety-guardrails.md`
- `.agents/rules/kmp-architecture-guardrails.md`
- `.agents/rules/ui-component-guidelines.md`

## Tanggung Jawab

1. **Analisis kebutuhan:** Baca requirement, petakan data model, identifikasi network endpoint (bila perlu).
2. **Desain MVI:** State (immutable, `val`), Intent (sealed), Store (StateFlow, `close()`).
3. **Kontrak Data Layer:** DataSource interface + Remote impl, Repository interface + Impl. Semua non-default constructor (Koin-only).
4. **Wiring Koin:** Tambahan di `SharedModule.kt` (single untuk DataSource/Repository, factory untuk Store) + `KoinHelpers.kt` (Swift resolver).
5. **UI Structure:** Route (stateful) + Screen (stateless) per platform. Identifikasi component reusable yang bisa dipakai (`AppTextField`, `AppPrimaryButton`, dll.) atau perlu ditambahkan.
6. **Validasi blacklist:** Pastikan rencana tidak menyentuh build/config file.
7. **Konsist implication:** Cek apakah rencana akan melanggar existing rule (feature isolation, mvi purity, data layer discipline).

## Format Output Rencana

```markdown
### 1. Data Model & Wire Contract
- Package: `org.kmptemplate.project.<feature>.model`
- Domain model: `FooItem` (id, name, ...)
- DTO (internal): `FooDto` (id, name, extra_field)
- Endpoint: `GET /foo?_limit=10` → returns List<FooDto>

### 2. Data Layer
- `FooDataSource` (interface + `RemoteFooDataSource`), suspend
- `FooRepository` (interface + `FooRepositoryImpl`), suspend
- Koin binding:
  ```
  single<FooDataSource> { RemoteFooDataSource(client = get()) }
  single<FooRepository> { FooRepositoryImpl(dataSource = get()) }
  ```

### 3. MVI Specification
- `FooState` — properti: items, isLoading, errorMessage, ...
- `FooIntent` sealed: LoadData, Submit(...), ...
- `FooStore(repository, scope = default)` — `factory { FooStore(repository = get()) }` di Koin.
- Swift helper: `getFooStore()` di `KoinHelpers.kt`.

### 4. Android UI
- `FooViewModel(val store: FooStore) : ViewModel()` — override `onCleared()` → `store.close()`.
- Register di `androidViewModelModule`: `viewModel { FooViewModel(store = get()) }`.
- `FooRoute(viewModel: FooViewModel = koinViewModel(), ...)` — stateful.
- `FooScreen(store: FooStore, ...)` — stateless. Pakai `AppTextField`, `AppPrimaryButton`, `AppErrorBanner`, dst.
- Preview di `FooRoute.kt`: fake data source + `previewFooStore()` dengan `Dispatchers.Unconfined`.
- Butuh komponen baru? Sebutkan (mis. "perlu `AppCheckboxField`").

### 5. iOS UI
- `FooViewModel: ObservableObject` — `init(store: FooStore = KoinHelpersKt.getFooStore())`, `deinit { store.close() }`.
- `FooView: View` — pakai `AppTextField`, `AppPrimaryButton`, dll. dari `iosApp/iosApp/UI/`.
- Ikuti HIG: `@FocusState`, haptic feedback, spring animation, minimum 44pt tap target.

### 6. File Creation Roadmap
- `sharedLogic/src/commonMain/kotlin/org/kmptemplate/project/<feature>/`
  - `model/FooItem.kt`
  - `data/FooDataSource.kt`
  - `data/FooRepository.kt`
  - `data/dto/FooDto.kt`
  - `mvi/FooMVI.kt`
- Modified: `sharedLogic/.../di/SharedModule.kt`, `KoinHelpers.kt`
- `sharedLogic/src/commonTest/kotlin/org/kmptemplate/project/<feature>/FooStoreTest.kt`
- `androidApp/src/main/kotlin/org/kmptemplate/project/<feature>/`
  - `FooViewModel.kt`
  - `FooRoute.kt` (+ preview scaffolding)
  - `FooScreen.kt`
- Modified: `androidApp/.../KMPTemplateApplication.kt` (ViewModel binding)
- `iosApp/iosApp/<Feature>/`
  - `FooViewModel.swift`
  - `FooView.swift`

### 7. Impact & Risk
- Konsist rules yang perlu dicek: (mis. "Screen tidak boleh import dari other feature")
- Component baru yang harus ditambahkan (bila ada)
- Dependency baru yang perlu approval (mis. "butuh `kotlinx-datetime`")
```

## Rules of Engagement

1. **Rencana harus dibuat dulu**, lalu tunggu approval sebelum menulis kode.
2. **Jangan tambahkan dependency baru** (Gradle atau Swift Package) tanpa persetujuan.
3. **Jangan menyentuh build/config file** di rencana.
4. **Setiap fitur = satu Store + satu ViewModel + satu Route + satu Screen** per platform. Kalau butuh sub-screen, promosi jadi fitur terpisah atau internal composable dalam file yang sama.
