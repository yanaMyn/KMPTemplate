# Claude Code Project Guidelines (CLAUDE.md)

Welcome to **KMPTemplate**. This project is a Kotlin Multiplatform application built with the **MVI Store Pattern** (StateFlow + Koin + Ktor) and Native UI (Compose for Android, SwiftUI for iOS).

Modules: `:sharedLogic` (KMP, shared) + `:androidApp` (Compose UI + entry) + `iosApp/` (Xcode, SwiftUI). See `README.md` for the current layout.

## Core Directives for Claude
Whenever you plan, write, or review code in this project, you **MUST** strictly adhere to the SOP and rules located in `.agents/`:

1. **Safety & Access Blacklist:** Read `.agents/rules/agent-safety-guardrails.md`
2. **Architecture Guardrails:** Read `.agents/rules/kmp-architecture-guardrails.md`
3. **UI Component Guidelines:** Read `.agents/rules/ui-component-guidelines.md` — Route/Screen split, design tokens, component API design, preview scaffolding
4. **Apple HIG Standards:** Read `.agents/rules/apple-hig-guidelines.md`
5. **Planning Strategy:** Follow `.agents/skills/kmp-planner/SKILL.md`
6. **Coding Standards:** Follow `.agents/skills/kmp-feature-generator/SKILL.md`
7. **Code Review Checklist:** Follow `.agents/skills/kmp-code-reviewer/SKILL.md`
8. **QA & Build Verification:** Follow `.agents/skills/kmp-qa-runner/SKILL.md`

## Definition of Done

A task is not complete until all three commands return success:

```bash
./gradlew :sharedLogic:build                # unit tests + Konsist arch tests
./gradlew :androidApp:assembleDebug         # Android APK compiles
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -sdk iphonesimulator \
  -destination 'generic/platform=iOS Simulator' build
```
