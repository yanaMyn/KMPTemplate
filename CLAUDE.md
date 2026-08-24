# Claude Code Project Guidelines (CLAUDE.md)

Welcome to **KMPTemplate**. This project is a Kotlin Multiplatform application built with the **MVI Store Pattern** and Native UI (Compose for Android, SwiftUI for iOS).

## Core Directives for Claude
Whenever you plan, write, or review code in this project, you **MUST** strictly adhere to the SOP and skills located in `.agents/`:

1. **Architecture & Safety Guardrails:** Check `.agents/rules/kmp-architecture-guardrails.md`
2. **Planning Strategy:** Follow `.agents/skills/kmp-planner/SKILL.md`
3. **Coding Standards:** Follow `.agents/skills/kmp-feature-generator/SKILL.md`
4. **Code Review Checklist:** Follow `.agents/skills/kmp-code-reviewer/SKILL.md`
5. **QA & Build Verification:** Follow `.agents/skills/kmp-qa-runner/SKILL.md`

## Safety Blacklist (STRICTLY FORBIDDEN TO EDIT)
- `build.gradle.kts` (Root, sharedLogic, sharedUI, androidApp)
- `local.properties`, `gradle.properties`, `settings.gradle.kts`
- `iosApp/iosApp.xcodeproj/` and dependency lock files

## Verification Command
Always verify your Kotlin changes by executing:
```bash
./gradlew :sharedLogic:build
```
If compilation fails, inspect the Gradle error output, fix the code within the feature directory, and re-run until build succeeds.
