# KMPTemplate

Kotlin Multiplatform template targeting **Android** (Jetpack Compose) and **iOS** (SwiftUI). Business logic and networking are shared through `:sharedLogic`; each platform owns its native UI.

## Stack

| Layer | Technology |
| :--- | :--- |
| Shared logic | Kotlin Multiplatform, Coroutines, `StateFlow` |
| Networking | Ktor 3.x + `kotlinx.serialization` — pointed at `https://jsonplaceholder.typicode.com` |
| DI | Koin 4.x |
| Swift interop | SKIE (sealed classes → Swift enums, `Flow` → `AsyncSequence`, `suspend` → `async`) |
| Android UI | Jetpack Compose + Material 3, `androidx.lifecycle.ViewModel` |
| iOS UI | SwiftUI + `@StateObject` view models |
| Architecture rules | Konsist (executed as JVM tests) |

## Modules

```
KMPTemplate/
├── sharedLogic/    Shared KMP: MVI stores, repositories, Ktor data sources, DI wiring
├── androidApp/     Android app: Compose UI, ViewModel wrappers, previews
└── iosApp/         Xcode project: SwiftUI views, Swift @MainActor view models
```

* **`:sharedLogic`** — the only KMP module. Contains `mvi/` (State/Intent/Store), `data/` (Repository, DataSource, DTOs), `network/HttpClientFactory.kt`, `di/SharedModule.kt`. Produces the `SharedLogic.framework` consumed by `iosApp`.
* **`:androidApp`** — Android application. Holds Compose screens, `koinViewModel()`-backed wrappers, `KMPTemplateApplication` (starts Koin), Compose `@Preview`s.
* **`iosApp/`** — Xcode project. Views bridge the shared stores via SKIE (`for await state in store.state`).

### The two-composable pattern per feature (`LoginRoute` vs `LoginScreen`)

Each feature has **two composables**:

* `LoginScreen(store: LoginStore, ...)` — **stateless**. Takes the store as a parameter, doesn't own its lifecycle, doesn't touch DI. Easy to preview and test with a fake store.
* `LoginRoute(viewModel: LoginViewModel = koinViewModel(), ...)` — **stateful root**. Resolves the ViewModel from Koin (with default) and hands `vm.store` to `LoginScreen`. This is the "app-facing" entry point that `App.kt` navigates to.

This is Google's recommended **stateful ↔ stateless split** for Compose ([docs](https://developer.android.com/develop/ui/compose/state)). The stateless one is unit-testable in isolation, previewable without a Koin container, and cheap to reason about. The stateful one is the boundary where DI and lifecycle live. Same pattern for `RegisterRoute`/`RegisterScreen` and `WalkthroughRoute`/`WalkthroughScreen`.

`viewModel` is a **parameter with default**, not a body-local `val`, so UI tests can override it: `LoginRoute(viewModel = fakeVm)`.

## Running

```bash
# Android
./gradlew :androidApp:assembleDebug            # build APK
./gradlew :androidApp:installDebug             # install on connected device / running emulator

# iOS — open in Xcode and press ⌘R
open iosApp/iosApp.xcodeproj
```

## Testing

```bash
./gradlew :sharedLogic:build                   # runs all JVM + iOS unit tests + Konsist arch tests
./gradlew :sharedLogic:testAndroidHostTest     # JVM unit tests only (stores + Konsist)
./gradlew :sharedLogic:iosSimulatorArm64Test   # iOS unit tests
```

`:sharedLogic:build` is the **Definition of Done** per `.agents/rules/agent-safety-guardrails.md`.

## Architecture rules (Konsist)

Enforced as regular JVM tests under `sharedLogic/src/androidHostTest/kotlin/.../architecture/`:

| Rule | Enforces |
| :--- | :--- |
| Feature isolation | `auth.*` and `walkthrough.*` cannot import each other |
| MVI purity | `mvi/` cannot import Ktor or DTOs |
| State immutability | `*State` classes are `data class` with only `val` properties |
| Sealed intents | `*Intent` classes are `sealed` (exhaustive `when`) |
| Data layer discipline | `RepositoryImpl` and concrete `DataSource` live under `.data.`; DTOs are `internal` |

Add a rule by dropping another `@Test` into the `architecture/` package — use `sharedLogicScope()` for module-local rules or `productionScope()` for cross-module rules (feature isolation).

## Configuration highlights

* **Koin bootstrap** — Android in `KMPTemplateApplication.onCreate()`, iOS in `iOSApp.init()` via `SharedModuleKt.startSharedKoin()`.
* **Single `HttpClient`** — declared once in `di/SharedModule.kt`, injected everywhere. Constructors don't have `HttpClient` defaults, so Koin is the only construction path.
* **Kotlin version pinned at 2.2.0** — SKIE (currently 0.10.14) does not yet support Kotlin 2.3+. See `.claude/memory/` for context. Ktor is at 3.3.3 for the same reason (later versions ship klibs with ABI 2.3.0).

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html).
