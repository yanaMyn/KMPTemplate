# KMP Architecture Guardrails

## 1. Project Overview & Tech Stack
* **Language:** Kotlin Multiplatform (Kotlin 2.0+), Swift 5.9+, Jetpack Compose, SwiftUI.
* **Architecture:** MVI (Model-View-Intent) Store Pattern.
* **Modules:**
  * `:sharedLogic` (`sharedLogic/src/commonMain`): Business logic, Repository, Models, MVI Stores.
  * `:sharedUI` (`sharedUI/src/commonMain`): Shared Compose/Multiplatform UI components.
  * `:androidApp` (`androidApp/src/main/kotlin`): Android Jetpack Compose Native UI.
  * `iosApp` (`iosApp/iosApp`): iOS SwiftUI Native UI.

---

## 2. Core Architectural Rules

1. **Shared Logic Purity:** No Android (`android.*`) or iOS (`UIKit.*`, `SwiftUI.*`) imports in `sharedLogic/src/commonMain`.
2. **MVI Store Pattern:**
   * `State`: Immutable data class dengan properti `val` only.
   * `Intent`: Sealed class yang merepresentasikan aksi user/sistem.
   * `Store`: Mengelola pembaruan state (`_state`), dispatch intent, dan mendukung subscription listener (`subscribe`).
3. **Anti Memory Leak:** Setiap `store.subscribe(...)` WAJIB mengembalikan dan memanggil `() -> Unit` unsubscribe function via `DisposableEffect` (Compose) atau `deinit` (Swift).
4. **No `runBlocking` in commonMain:** Selalu gunakan coroutine non-blocking atau listener dispatcher.
5. **Native UI Isolation:**
   * Android views mengkonsumsi state dan dispatch intent ke `Store`.
   * iOS views menjembatani `Store` menggunakan `@MainActor ObservableObject` untuk mempublikasikan perubahan state ke SwiftUI.
6. **iOS UI/UX:** Seluruh tampilan iOS SwiftUI WAJIB mematuhi Apple Human Interface Guidelines. Lihat `apple-hig-guidelines.md`.
