---
name: kmp-feature-generator
description: >-
  Bertanggung jawab untuk mengeksekusi penulisan kode fitur baru pada proyek KMP dengan pola arsitektur MVI dan Native UI (Jetpack Compose untuk Android dan SwiftUI untuk iOS). Gunakan ketika menulis atau merekayasa kode Kotlin dan Swift.
---

# Agent Role: KMP Code Generator & Executor

## 1. Tata Cara & Alur Kerja (Workflow)
Saat mengeksekusi pembuatan fitur, ikuti urutan langkah berikut:
1. **Shared Logic (`sharedLogic/src/commonMain/kotlin/org/kmptemplate/project/<feature>/`)**:
   * `model/<Feature>Item.kt`: Data class model.
   * `data/<Feature>Repository.kt`: Repository interface & implementation.
   * `mvi/<Feature>MVI.kt`: `State`, `Intent`, dan `Store` class dengan fungsi `dispatch` dan `subscribe`.
2. **Android UI (`androidApp/src/main/kotlin/org/kmptemplate/project/<feature>/`)**:
   * Buat composable screen menggunakan Jetpack Compose.
   * Hubungkan `Store.subscribe` dengan Compose State (`remember { mutableStateOf(...) }`).
3. **iOS UI (`iosApp/iosApp/<Feature>/`)**:
   * Buat SwiftUI View yang mematuhi **Apple Human Interface Guidelines (HIG)**:
     - Gunakan `@FocusState` & Keyboard Toolbar untuk pengalaman pengetikan native.
     - Integrasikan **Haptic Feedback** (`UIImpactFeedbackGenerator`, `UINotificationFeedbackGenerator`) pada tap, error, dan success.
     - Terapkan animasi spring yang mulus (`withAnimation(.spring(...))`).
     - Gunakan pola modalitas yang tepat (`.sheet` atau `.fullScreenCover`) dengan gesture dismissal.
     - Gunakan semantic colors (`Color(.systemGroupedBackground)`, dll.) dan Dynamic Type.
   * Buat `ObservableObject` wrapper di Swift (`@MainActor`) untuk mengamati perubahan state dari KMP `Store`.

## 2. Template Kode MVI Store di `sharedLogic`
```kotlin
package org.kmptemplate.project.example.mvi

data class ExampleState(
    val items: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class ExampleIntent {
    data object LoadData : ExampleIntent()
    data class SubmitItem(val text: String) : ExampleIntent()
}

class ExampleStore {
    private var _state = ExampleState(isLoading = true)
    val state: ExampleState get() = _state
    private val listeners = mutableListOf<(ExampleState) -> Unit>()

    init {
        dispatch(ExampleIntent.LoadData)
    }

    fun dispatch(intent: ExampleIntent) {
        when (intent) {
            is ExampleIntent.LoadData -> handleLoadData()
            is ExampleIntent.SubmitItem -> handleSubmitItem(intent.text)
        }
    }

    private fun updateState(newState: ExampleState) {
        _state = newState
        listeners.forEach { it(_state) }
    }

    fun subscribe(listener: (ExampleState) -> Unit): () -> Unit {
        listeners.add(listener)
        listener(_state)
        return { listeners.remove(listener) }
    }

    private fun handleLoadData() {
        // Logika bisnis / repository
        updateState(_state.copy(isLoading = false, items = listOf("Sample 1", "Sample 2")))
    }

    private fun handleSubmitItem(text: String) {
        updateState(_state.copy(items = _state.items + text))
    }
}
```

## 3. Batasan Akses File (Blacklist)
JANGAN PERNAH menyentuh file berikut:
- `.gradle/`, `.idea/`, `local.properties`, `gradle.properties`
- `build.gradle.kts` (Root, sharedLogic, sharedUI, androidApp)
- `iosApp/iosApp.xcodeproj/`, `Podfile.lock`, `Package.resolved`
