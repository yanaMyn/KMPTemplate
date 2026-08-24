# Agent Safety Guardrails

Aturan keamanan ini berlaku **universal** untuk semua AI Agent (Antigravity, Claude, Cursor, Kiro, Windsurf, Copilot) yang bekerja di repositori ini. Pelanggaran terhadap aturan di bawah ini **TIDAK DAPAT DITOLERANSI**.

---

## 🔴 BAGIAN 1: Blacklist Akses File & Folder (DILARANG READ, OPEN, ATAU MODIFY)

Berikut adalah daftar file dan folder yang **dilarang keras** dibaca, dibuka, dimodifikasi, dihapus, atau direferensikan dalam kondisi apapun.

### 1a. File & Folder Privat Pemilik
| Path | Alasan |
| :--- | :--- |
| `📂 private/` (dan SELURUH isi di dalamnya) | Folder privat pemilik. Berisi data rahasia. **DILARANG KERAS diakses dalam kondisi apapun.** |
| `📄 private.txt` (di root project) | Catatan privat pemilik. **DILARANG membaca atau membuka.** |

### 1b. Konfigurasi & Kredensial Sensitif
| Path / Pattern | Alasan |
| :--- | :--- |
| `📄 local.properties` | Berisi path SDK spesifik mesin dan password keystore. |
| `📄 *.jks`, `📄 *.keystore`, `📄 *.p12`, `📄 *.mobileprovision` | Sertifikat signing dan provisioning profile. |
| `📄 .env`, `📄 .env.*` | File environment variables yang mungkin berisi API keys atau secrets. |
| `📄 google-services.json`, `📄 GoogleService-Info.plist` | Kredensial Firebase produksi. |

### 1c. Build System & Konfigurasi Kritis
| Path / Pattern | Alasan |
| :--- | :--- |
| `📄 gradle.properties` | JVM memory settings dan versi dependency. |
| `📄 build.gradle.kts` (Root & submodule) | Build scripts kritis; perubahan otomatis merusak CocoaPods / Xcode sync. |
| `📄 settings.gradle.kts` | Module registry. |
| `📂 iosApp/iosApp.xcodeproj/` | File konfigurasi Xcode internal (XML/PBX) — rentan korup jika diedit teks manual. |
| `📄 Podfile.lock`, `📄 Package.resolved`, `📄 gradle-wrapper.jar` | Dependency lock files. |

### 1d. Cache & Build Output (Jangan Baca / Jangan Sentuh)
| Path | Alasan |
| :--- | :--- |
| `📂 .gradle/`, `📂 .idea/`, `📂 build/`, `📂 .kotlin/` | Artefak build dan cache IDE. Tidak relevan untuk pekerjaan agen. |

---

## 🔴 BAGIAN 2: Perintah Terminal Terlarang

Agent **DILARANG KERAS** menjalankan perintah berikut karena berisiko kehilangan data permanen atau kerusakan sistem:

| Perintah Terlarang | Risiko |
| :--- | :--- |
| `rm -rf <apapun>` (terutama dengan wildcard) | Penghapusan file permanen — tidak bisa di-undo. |
| `git reset --hard` | Menghapus seluruh pekerjaan yang belum di-commit. |
| `git clean -fdx`, `git checkout .` | Mengembalikan file ke state lama, menghapus perubahan lokal. |
| `git push --force` | Menimpa remote history tanpa persetujuan pemilik. |
| `sudo <perintah apapun>` | Eskalasi hak akses ke root — risiko keamanan sistem. |
| Perintah di luar direktori workspace | Akses filesystem di luar scope proyek. |
| Menambah dependency baru (Gradle/SPM) tanpa izin | Mengubah dependency graph tanpa persetujuan eksplisit. |

### ✅ Perintah Terminal yang Diizinkan
```bash
./gradlew :sharedLogic:build        # Verifikasi kompilasi KMP
./gradlew :sharedLogic:allTests     # Jalankan unit test
./gradlew :sharedUI:build           # Verifikasi kompilasi sharedUI
git status                          # Cek status perubahan (read-only)
git diff                            # Lihat diff perubahan (read-only)
git log -n 10                       # Lihat 10 commit terakhir (read-only)
```

---

## 🟡 BAGIAN 3: Aturan Perilaku & Workflow

1. **Rencana Dulu, Baru Eksekusi:** Untuk fitur baru atau refactoring yang menyentuh lebih dari 2 file, agent WAJIB membuat dokumen rencana arsitektur (MVI specs) dan menunggu persetujuan eksplisit dari pemilik sebelum menulis kode apapun.
2. **Definisi Selesai:** Sebuah tugas BELUM dianggap selesai sampai `./gradlew :sharedLogic:build` mengembalikan hasil `BUILD SUCCESSFUL`.
3. **Jangan Hapus Kode yang Ada:** Agent dilarang menghapus fungsi yang sudah ada atau menggantinya dengan komentar `// TODO: implement later`.
4. **Jangan Tambah Dependency Sembarangan:** Agent dilarang menambahkan library baru (Gradle atau Swift Package) tanpa persetujuan eksplisit dari pemilik.
