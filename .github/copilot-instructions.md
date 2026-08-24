# GitHub Copilot Instructions for KMPTemplate

## Project Architecture
This repository is a **Kotlin Multiplatform (KMP)** project with:
- Business Logic: `sharedLogic/src/commonMain/kotlin` using MVI Store pattern.
- Android UI: `androidApp/src/main/kotlin` using Jetpack Compose.
- iOS UI: `iosApp/iosApp` using SwiftUI.

## Instructions
1. Follow all design patterns defined in `.agents/skills/kmp-feature-generator/SKILL.md`.
2. Do not modify build files (`build.gradle.kts`, `gradle.properties`, `local.properties`).
3. Maintain immutable states in data classes and exhaustive sealed classes for intents.
4. When writing tests, place them in `sharedLogic/src/commonTest/kotlin` following `.agents/skills/kmp-qa-runner/SKILL.md`.
