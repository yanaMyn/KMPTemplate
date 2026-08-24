# Walkthrough: Universal Multi-Agent Architecture (Single Source of Truth)

Penerapan sistem **Multi-Agent Universal** pada repositori **KMPTemplate** telah berhasil diselesaikan dan divalidasi dengan kompilasi Gradle (`BUILD SUCCESSFUL`).

---

## 1. Struktur File yang Telah Dibangun

```text
KMPTemplate/
├── .agents/                                    # 🌟 Source of Truth Utama
│   ├── rules/
│   │   └── kmp-architecture-guardrails.md      # Batasan arsitektur & Blacklist file
│   └── skills/
│       ├── kmp-planner/SKILL.md                # Agen 1: Desain Arsitektur & MVI State/Intent
│       ├── kmp-feature-generator/SKILL.md      # Agen 2: Penulisan Kode Kotlin MVI & Native UI
│       ├── kmp-code-reviewer/SKILL.md          # Agen 3: Audit Kepatuhan MVI & Keamanan
│       └── kmp-qa-runner/SKILL.md              # Agen 4: Verifikasi Kompilasi & Unit Testing
│
├── AGENTS.md                                   # Jembatan Antigravity / Agentic IDE
├── CLAUDE.md                                   # Jembatan Claude Code CLI
├── .cursorrules                                # Jembatan Cursor / Kiro (Fallback)
├── .cursor/rules/kmp-rules.mdc                 # Jembatan Cursor / Kiro (Modern Rules)
├── .github/copilot-instructions.md             # Jembatan VS Code / GitHub Copilot
├── .windsurfrules                              # Jembatan Windsurf IDE
└── iosApp/PROMPT_GUIDE.md                      # Jembatan Xcode (Apple Intelligence / Copilot)
```

---

## 2. Cara Penggunaan di Setiap Tool (How to Use)

### 🤖 A. Antigravity IDE / Antigravity 2.0
* **Otomatis (Progressive Disclosure):** Cukup ketik perintah Anda di chat:
  > *"Tolong buatkan fitur Notifikasi. Tunjukkan rancangan arsitektur MVI-nya terlebih dahulu, lalu setelah kode dibuat, jalankan checklist dari Code Reviewer dan tampilkan hasil verifikasi Gradle QA-nya."*
  
  Antigravity akan otomatis mengaktifkan skill [.agents/skills/kmp-feature-generator](.agents/skills/kmp-feature-generator/SKILL.md) dan menjalankan verifikasi kompilasi.
* **Mode Otonom Penuh:** Gunakan `/goal` untuk penanganan fitur besar secara mendalam:
  > `/goal Buatkan fitur Bookmark Artikel dan pastikan build gradle sukses`

---

### 💻 B. Claude Code (CLI Terminal)
Jalankan Claude Code langsung dari terminal root proyek:
```bash
claude
```
* Claude Code otomatis membaca [CLAUDE.md](CLAUDE.md) setiap kali sesi dimulai.
* Anda bisa langsung mengetik:
  > `Tolong rancang dan implementasikan fitur Keranjang Belanja sesuai SOP di .agents/`

---

### ⚡ C. Cursor / Kiro IDE
* Cursor dan Kiro otomatis membaca file [.cursorrules](.cursorrules) dan [.cursor/rules/kmp-rules.mdc](.cursor/rules/kmp-rules.mdc).
* Saat menggunakan fitur **Composer / Chat (`⌘+I` / `⌘+L`)**, AI otomatis mematuhi aturan MVI, path `sharedLogic/`, dan tidak menyentuh file blacklist.

---

### 🟦 D. VS Code (GitHub Copilot)
* GitHub Copilot Chat secara native membaca [.github/copilot-instructions.md](.github/copilot-instructions.md).
* Buka Copilot Chat (`Ctrl+Alt+I` / `⌘+Shift+I`) dan ketik:
  > `@workspace Buatkan MVI Store untuk fitur Notifikasi`

---

### 🌊 E. Windsurf IDE
* Windsurf otomatis mendeteksi [.windsurfrules](.windsurfrules) saat Cascade Agent aktif.
* Cukup minta Cascade membuat fitur baru; Cascade akan mengikuti panduan modul `sharedLogic/`.

---

### 🍎 F. Xcode (Apple Intelligence / Copilot for Xcode)
* Saat Anda membuka `iosApp.xcodeproj` di Xcode dan membuat tampilan SwiftUI:
* Gunakan pola wrapper Observable di [iosApp/PROMPT_GUIDE.md](iosApp/PROMPT_GUIDE.md) agar SwiftUI dapat mengamati State KMP secara reaktif dan aman di main thread.

---

## 3. Hasil Validasi
- **Pembersihan File Lama:** Berhasil menghapus file non-standar `workflows/` dan `agent-loop.sh`.
- **Kompilasi Gradle:** `./gradlew :sharedLogic:build` $\rightarrow$ **BUILD SUCCESSFUL**.
