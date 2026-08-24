# KMP Architecture Guardrails & Safety Protocols

## 1. Project Overview & Tech Stack
* **Language:** Kotlin Multiplatform (Kotlin 2.0+), Swift 5.9+, Jetpack Compose, SwiftUI.
* **Architecture:** MVI (Model-View-Intent) Store Pattern.
* **Modules:**
  * `:sharedLogic` (`sharedLogic/src/commonMain`): Business logic, Repository, Models, MVI Stores.
  * `:sharedUI` (`sharedUI/src/commonMain`): Shared Compose/Multiplatform UI components (if used).
  * `:androidApp` (`androidApp/src/main/kotlin`): Android Jetpack Compose Native UI.
  * `iosApp` (`iosApp/iosApp`): iOS SwiftUI Native UI.

---

## 2. File Blacklist (DO NOT MODIFY / DO NOT TOUCH)
To prevent build system failure and project corruption, NEVER create, edit, or delete the following:

| Path / Pattern | Reason |
| :--- | :--- |
| `📂 .gradle/`, `📂 .idea/`, `📂 build/` | IDE & Gradle cache and build outputs. |
| `📄 local.properties` | Contains machine-specific SDK paths and secret keys. |
| `📄 gradle.properties` | JVM memory settings and dependency versions. |
| `📄 build.gradle.kts` (Root & submodules) | Build scripts; automated changes break CocoaPods / Xcode sync. |
| `📄 settings.gradle.kts` | Module registry. |
| `📂 iosApp/iosApp.xcodeproj/` | Internal Xcode configuration files (XML/PBX). |
| `📄 iosApp/Podfile.lock`, `📄 Package.resolved` | Dependency lock files. |

---

## 3. Core Architectural Rules
1. **Shared Logic Purity:** No Android (`android.*`) or iOS (`UIKit.*`, `SwiftUI.*`) imports in `sharedLogic/src/commonMain`.
2. **MVI Store Pattern:**
   * `State`: Immutable data class.
   * `Intent`: Sealed class representing user/system actions.
   * `Store`: Manages state updates (`_state`), dispatches intents, and supports listener subscriptions (`subscribe`).
3. **No `runBlocking` in commonMain:** Always use non-blocking coroutines or listener dispatchers.
4. **Native UI Isolation:** 
   * Android views consume state and dispatch intents to `Store`.
   * iOS views bridge `Store` using an `@ObservableObject` (or `@Observable` in Swift) to publish state changes to SwiftUI.
