# Walkthrough: Universal Multi-Agent Architecture (Single Source of Truth)

Sistem **Multi-Agent Universal** pada repositori **KMPTemplate**. Setiap AI-agent tool (Claude Code, Cursor, Copilot, Windsurf, Antigravity, dll.) menunjuk ke folder `.agents/` yang sama sebagai satu-satunya sumber kebenaran.

Divalidasi via:
```bash
./gradlew :sharedLogic:build              # unit tests + Konsist architecture tests
./gradlew :androidApp:assembleDebug
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -sdk iphonesimulator \
  -destination 'generic/platform=iOS Simulator' build
```

---

## 1. Struktur File

```text
KMPTemplate/
├── .agents/                                    # 🌟 Source of Truth Utama
│   ├── rules/
│   │   ├── agent-safety-guardrails.md          # Blacklist file & command terlarang
│   │   ├── kmp-architecture-guardrails.md      # MVI + StateFlow, Koin, Ktor, lifecycle
│   │   ├── ui-component-guidelines.md          # Route/Screen split, tokens, larangan duplikasi UI
│   │   └── apple-hig-guidelines.md             # Standar UX iOS (SwiftUI)
│   └── skills/
│       ├── kmp-planner/SKILL.md                # Agen 1: Desain MVI + roadmap file
│       ├── kmp-feature-generator/SKILL.md      # Agen 2: Penulisan kode Kotlin/Swift
│       ├── kmp-code-reviewer/SKILL.md          # Agen 3: Audit MVI, Koin, UI discipline
│       └── kmp-qa-runner/SKILL.md              # Agen 4: Build + Konsist + xcodebuild
│
├── CLAUDE.md                                   # Jembatan Claude Code CLI
├── AGENTS.md                                   # Jembatan Antigravity / IDE agentic
├── .cursorrules                                # Jembatan Cursor / Kiro (fallback lama)
├── .cursor/rules/kmp-rules.mdc                 # Jembatan Cursor (modern rules)
├── .github/copilot-instructions.md             # Jembatan VS Code / GitHub Copilot
├── .windsurfrules                              # Jembatan Windsurf IDE
├── iosApp/PROMPT_GUIDE.md                      # Jembatan Xcode (Apple Intelligence / Copilot for Xcode)
│
├── sharedLogic/                                # KMP module: MVI stores, Ktor, Koin
├── androidApp/                                 # Compose UI + ViewModel wrappers + previews
└── iosApp/                                     # Xcode project: SwiftUI + Swift @MainActor VMs
```

Setiap file "Jembatan" berisi daftar `.agents/rules/*.md` + `.agents/skills/*/SKILL.md` yang identik, plus **Definition of Done** yang sama. Tambah/ubah aturan di `.agents/`, semua tool otomatis ikut.

---

## 2. Cara Penggunaan di Setiap Tool

### 🤖 A. Antigravity IDE / Antigravity 2.0
* **Otomatis (Progressive Disclosure):** ketik perintah di chat:
  > *"Tolong buatkan fitur Notifikasi. Tunjukkan rancangan MVI dulu, lalu setelah kode dibuat, jalankan checklist Reviewer dan tampilkan hasil QA build."*
* **Mode otonom:** `/goal Buatkan fitur Bookmark Artikel dan pastikan build sukses`
* Antigravity membaca `AGENTS.md` → mengarah ke skill di `.agents/skills/`.

### 💻 B. Claude Code (CLI)
```bash
claude
```
* Membaca `CLAUDE.md` di setiap sesi.
* Contoh prompt: `Tolong rancang dan implementasikan fitur Keranjang Belanja sesuai SOP di .agents/`

### ⚡ C. Cursor / Kiro IDE
* Otomatis membaca `.cursorrules` dan `.cursor/rules/kmp-rules.mdc`.
* Fitur Composer / Chat (`⌘+I` / `⌘+L`) mematuhi aturan MVI, path `:sharedLogic`, blacklist file, dan UI Component Guidelines.

### 🟦 D. VS Code (GitHub Copilot)
* Membaca `.github/copilot-instructions.md` secara native.
* Buka Copilot Chat (`⌘+Shift+I`): `@workspace Buatkan MVI Store + Route/Screen untuk fitur Notifikasi`

### 🌊 E. Windsurf IDE
* Cascade Agent otomatis mendeteksi `.windsurfrules`.
* Ikuti panduan module `:sharedLogic` + `ui/components/` di `androidApp`.

### 🍎 F. Xcode (Apple Intelligence / Copilot for Xcode)
* Buka `iosApp/iosApp.xcodeproj`.
* Ikuti pola `@MainActor ObservableObject` + `KoinHelpersKt.getFooStore()` untuk view model, plus komponen di `iosApp/iosApp/UI/Components/`. Detail di `iosApp/PROMPT_GUIDE.md`.

---

## 3. Alur Menambah / Mengubah Aturan

Karena setiap tool ↔ satu file jembatan ↔ `.agents/`, perubahan aturan cukup dilakukan sekali:

1. **Edit** file target di `.agents/rules/` atau `.agents/skills/*/SKILL.md`.
2. Bila menambah rule file baru (mis. `.agents/rules/testing-guidelines.md`), **update semua bridge file**:
   - `CLAUDE.md`
   - `AGENTS.md`
   - `.cursorrules`
   - `.cursor/rules/kmp-rules.mdc`
   - `.github/copilot-instructions.md`
   - `.windsurfrules`
3. **Validasi Konsist tetap hijau:** `./gradlew :sharedLogic:testAndroidHostTest`.
4. **Refresh WALKTHROUGH.md** (file ini) supaya diagram file tetap akurat.

---

## 4. Kontrak Antarmodul (Ringkas)

- `:sharedLogic` **tidak boleh** meng-import `android.*` / `androidx.*` / `UIKit.*` / `SwiftUI.*`.
- Package `.mvi.*` **tidak boleh** meng-import `io.ktor.*` atau `.data.dto.*`. Diaudit otomatis oleh Konsist (`MviPurityTest`).
- Fitur `auth.*` ⊥ `walkthrough.*` (Konsist `FeatureBoundaryTest`).
- `HttpClient` = satu `single` di Koin. Constructor DataSource / Repository / Store **tidak boleh** memiliki default `= createHttpClient()` / `= AuthRepositoryImpl()`.
- Compose Route ↔ Screen split wajib per fitur. Feature `*Screen.kt` **tidak boleh** meng-import `OutlinedTextField` / `SecureField` / `Button` mentah — semua lewat komponen `App*` di `ui/components/`.

Detail: `.agents/rules/kmp-architecture-guardrails.md`, `.agents/rules/ui-component-guidelines.md`.
