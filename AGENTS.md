# Project AI Agent Instructions (AGENTS.md)

Selamat datang di repositori **KMPTemplate**. Proyek ini menggunakan arsitektur **Kotlin Multiplatform (KMP) MVI + StateFlow + Koin + Ktor**, dengan pemisahan mutlak antara logika bersama (`:sharedLogic`) dan antarmuka bawaan (`:androidApp` dengan Compose, `iosApp/` dengan SwiftUI).

## Source of Truth Panduan & SOP
Seluruh aturan dan alur kerja agen terpusat di folder `.agents/`:

1. **Keamanan & Blacklist:** Baca [.agents/rules/agent-safety-guardrails.md](.agents/rules/agent-safety-guardrails.md)
2. **Guardrails Arsitektur:** Baca [.agents/rules/kmp-architecture-guardrails.md](.agents/rules/kmp-architecture-guardrails.md)
3. **UI Component Guidelines:** Baca [.agents/rules/ui-component-guidelines.md](.agents/rules/ui-component-guidelines.md) — Route/Screen split, design tokens, larangan duplikasi UI, konvensi API komponen
4. **Apple HIG & iOS UI/UX Standards:** Baca [.agents/rules/apple-hig-guidelines.md](.agents/rules/apple-hig-guidelines.md)
5. **Perencanaan Arsitektur (Planner):** Baca [.agents/skills/kmp-planner/SKILL.md](.agents/skills/kmp-planner/SKILL.md)
6. **Eksekusi Penulisan Kode (Generator):** Baca [.agents/skills/kmp-feature-generator/SKILL.md](.agents/skills/kmp-feature-generator/SKILL.md)
7. **Audit & Review Kode (Reviewer):** Baca [.agents/skills/kmp-code-reviewer/SKILL.md](.agents/skills/kmp-code-reviewer/SKILL.md)
8. **Kompilasi & Testing (QA):** Baca [.agents/skills/kmp-qa-runner/SKILL.md](.agents/skills/kmp-qa-runner/SKILL.md)

## Definition of Done

Tugas **belum selesai** sampai ketiga perintah ini `PASSED`:

```bash
./gradlew :sharedLogic:build              # unit tests + Konsist architecture tests
./gradlew :androidApp:assembleDebug       # Android APK compiles
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -sdk iphonesimulator \
  -destination 'generic/platform=iOS Simulator' build
```
