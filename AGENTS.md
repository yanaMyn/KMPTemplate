# Project AI Agent Instructions (AGENTS.md)

Selamat datang di repositori **KMPTemplate**. Proyek ini menggunakan arsitektur **Kotlin Multiplatform (KMP) MVI** dengan pemisahan mutlak antara logika bersama (`sharedLogic/`) dan antarmuka bawaan (`androidApp/` dengan Compose, `iosApp/` dengan SwiftUI).

## Source of Truth Panduan & SOP
Seluruh aturan dan alur kerja agen terpusat di folder `.agents/`:

1. **Guardrails & Blacklist File:** Baca [.agents/rules/kmp-architecture-guardrails.md](.agents/rules/kmp-architecture-guardrails.md)
2. **Perencanaan Arsitektur (Planner):** Baca [.agents/skills/kmp-planner/SKILL.md](.agents/skills/kmp-planner/SKILL.md)
3. **Eksekusi Penulisan Kode (Generator):** Baca [.agents/skills/kmp-feature-generator/SKILL.md](.agents/skills/kmp-feature-generator/SKILL.md)
4. **Audit & Review Kode (Reviewer):** Baca [.agents/skills/kmp-code-reviewer/SKILL.md](.agents/skills/kmp-code-reviewer/SKILL.md)
5. **Kompilasi & Testing (QA):** Baca [.agents/skills/kmp-qa-runner/SKILL.md](.agents/skills/kmp-qa-runner/SKILL.md)

## Aturan Kritis (DO NOT VIOLATE)
* **DILARANG KERAS** memodifikasi: `build.gradle.kts`, `local.properties`, `gradle.properties`, `settings.gradle.kts`, `iosApp.xcodeproj/`, `Podfile.lock`, `Package.resolved`.
* Selalu validasi perubahan dengan menjalankan `./gradlew :sharedLogic:build` sebelum menandai tugas selesai.
