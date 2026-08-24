# Apple Human Interface Guidelines (HIG) & iOS UI/UX Standards

Panduan ini mendefinisikan standar antarmuka, transisi, dan interaksi native untuk aplikasi iOS (`iosApp/`) pada proyek KMPTemplate agar memberikan pengalaman khas Apple yang mulus, responsif, dan elegan.

---

## 1. Navigasi & Pola Presentasi (Navigation & Modality)
- **Modality yang Tepat:** Gunakan `.sheet` untuk task sementara (seperti login, form input terisolasi) dengan *grabber* / interactive swipe down, atau `.fullScreenCover` untuk alur imersif seperti onboarding/walkthrough.
- **Transisi Native:** Hindari transisi mendadak (*abrupt*). Gunakan `withAnimation(.spring(response: 0.35, dampingFraction: 0.8))` atau transisi asimetris (`.asymmetric(insertion: ..., removal: ...)`).
- **Interactive Dismissal:** Selalu sediakan tombol dismiss yang jelas di NavigationBar (misal `leading` chevron atau `trailing` Close button) serta dukung drag-to-dismiss gesture pada sheet.

---

## 2. Umpan Balik & Interaksi Haptik (Haptic Feedback)
Integrasikan umpan balik haptik native untuk memperkaya interaksi pengguna:
- **Button Tap & Selection:** `UIImpactFeedbackGenerator(style: .light).impactOccurred()` atau `.selectionChanged()`.
- **Success Event:** `UINotificationFeedbackGenerator().notificationOccurred(.success)` (misal: login sukses, onboarding selesai).
- **Error Event:** `UINotificationFeedbackGenerator().notificationOccurred(.error)` (misal: validasi gagal, kredensial salah).
- **Warning Event:** `UINotificationFeedbackGenerator().notificationOccurred(.warning)`.

---

## 3. Form Input & Pengelolaan Keyboard (Focus & Keyboard UX)
- **`@FocusState` Management:** Gunakan `@FocusState enum Field { case email, password }` untuk navigasi antar-field yang mulus saat menekan "Next/Return" di keyboard.
- **Keyboard Toolbar:** Sediakan tombol `ToolbarItemGroup(placement: .keyboard)` dengan aksi "Selesai" (*Done*) atau "Sebelumnya / Selanjutnya" untuk mempermudah dismiss keyboard.
- **Autocapitalization & Content Types:** 
  - Email: `.textContentType(.username)` / `.emailAddress`, `.keyboardType(.emailAddress)`, `.textInputAutocapitalization(.never)`, `.autocorrectionDisabled()`.
  - Password: `.textContentType(.password)`, `.textInputAutocapitalization(.never)`, `.autocorrectionDisabled()`.

---

## 4. Visual Design & Tipografi Apple Native
- **Dynamic Type:** Gunakan semantic text styles (`.largeTitle`, `.title2`, `.headline`, `.subheadline`, `.footnote`) daripada ukuran hardcoded `.fontSize(...)`.
- **Semantic Colors & Vibrancy:** Manfaatkan `Color(uiColor: .systemBackground)`, `.secondarySystemBackground`, `.systemGroupedBackground`, `Color.accentColor`, dan `.primary` / `.secondary` untuk dukungan Dark Mode otomatis 100%.
- **Corner Radii & Materials:** Gunakan radius standar Apple (12-16pt) dengan continuous corner curves (`RoundedRectangle(cornerRadius: 16, style: .continuous)`) atau `.ultraThinMaterial` / `.thinMaterial`.
- **Minimum Tap Targets:** Seluruh elemen interaktif harus memiliki area sentuh minimal **44x44 pt** sesuai standar Apple HIG.

---

## 5. Micro-Animations & State Changes
- **Interactive Button States:** Gunakan `ButtonStyle` dengan scaling/opacity feedback (misal `configuration.isPressed ? 0.96 : 1.0`).
- **Loading Indicators:** Gunakan `ProgressView()` native dengan tint yang sesuai, tanpa mengunci atau membekukan UI thread.
- **Inline Error Feedback:** Tampilkan pesan error secara kontekstual dengan transisi halus (`.transition(.move(edge: .top).combined(with: .opacity))`).
