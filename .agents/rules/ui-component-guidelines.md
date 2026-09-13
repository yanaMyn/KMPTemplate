# UI Component Guidelines

Aturan universal untuk semua Compose (Android) & SwiftUI (iOS) UI di repositori ini. Berlaku untuk setiap agent yang menulis, memodifikasi, atau me-review kode UI.

Referensi lokasi:
- Android tokens & primitives: `androidApp/src/main/kotlin/org/kmptemplate/project/ui/`
- iOS tokens & primitives: `iosApp/iosApp/UI/`

---

## 🔴 BAGIAN 1: Struktur Wajib (Non-Negotiable)

### 1.1 Route ↔ Screen Split

Setiap fitur **WAJIB** memiliki dua composable:

| Peran | Nama | Isi | Tugas |
| :--- | :--- | :--- | :--- |
| Stateful root | `FooRoute` | Resolve ViewModel dari Koin → hand off store ke `FooScreen` | DI & lifecycle |
| Stateless leaf | `FooScreen(store: FooStore, ...)` | `state.collectAsState()` + rendering | UI pura-pura |

- `FooScreen` **DILARANG** memanggil `koinViewModel()`, `koinInject()`, `navController.*`, atau menyimpan session/nav state.
- `FooRoute` **HARUS** menerima `viewModel: FooViewModel = koinViewModel()` sebagai **parameter default**, bukan `val` di dalam body. Alasan: caller (UI test) bisa mengoper ViewModel palsu.
- iOS analog: `FooView` (SwiftUI) memegang `@StateObject var viewModel = FooViewModel()`; view-body memakai `viewModel.state.*` seperti FooScreen memakai `store.state.*`.

### 1.2 State Ownership

- Component leaf (`AppTextField`, `AppPasswordField`, dst.) **DILARANG** menyimpan `remember { mutableStateOf(...) }` untuk data yang seharusnya dimiliki store (isVisible, focus, error, value).
- Hoist state ke atas: component menerima `value + onValueChange`, `isVisible + onToggleVisibility`, `errorMessage: String?`.
- iOS analog: pakai `@Binding` untuk value, bukan `@State` internal.

### 1.3 Design Tokens, Bukan Hardcoded

Semua nilai dimensi & bentuk **HARUS** dari token:
- Android: `AppSpacing.*`, `AppShapes.*`, `AppSizes.*` di `ui/theme/AppTheme.kt`
- iOS: `AppSpacing`, `AppCornerRadius`, `AppSizes` di `UI/Theme/AppTheme.swift`

**❌ SALAH:** `Modifier.padding(16.dp)`, `RoundedCornerShape(14.dp)`, `.frame(height: 52)`
**✅ BENAR:** `Modifier.padding(AppSpacing.lg)`, `RoundedCornerShape(AppShapes.cornerLarge)`, `.frame(height: AppSizes.buttonHeight)`

Warna & tipografi **HARUS** dari `MaterialTheme.colorScheme.*` / `MaterialTheme.typography.*` (Android) atau semantic system colors seperti `Color.accentColor`, `Color(uiColor: .systemGroupedBackground)` (iOS). Jangan hardcode hex code.

### 1.4 No UI Duplication — Extract to Component

**Setiap pola visual/behavior yang muncul di ≥2 tempat WAJIB dipromosikan ke komponen** di `ui/components/` (Android) atau `UI/Components/` (iOS). Copy-paste UI antar fitur adalah pelanggaran yang setara dengan copy-paste business logic.

Yang termasuk "pola" di sini:
- Kumpulan modifier & container yang membentuk satu unit visual (mis. field bertopi label + border adaptive + error inline).
- Kombinasi widget dengan konfigurasi yang sama di beberapa layar (mis. submit button 52.dp + rounded 14 + spinner).
- Layout scaffolding yang berulang (mis. header bulat + title + subtitle).

#### Alur wajib ketika akan menulis UI baru

1. **Cek dulu** `androidApp/src/main/kotlin/org/kmptemplate/project/ui/components/` (Android) atau `iosApp/iosApp/UI/Components/` (iOS). Apakah sudah ada komponen yang cocok?
2. Jika **ada**: pakai komponen tersebut. Kalau perlu tambahan behavior, pertimbangkan ekstensi lewat parameter (patuhi §2.1) sebelum bikin komponen baru.
3. Jika **tidak ada tapi pola akan dipakai ulang**: buat komponen baru di `ui/components/` sekarang, sebelum menulis kode layar.
4. Jika **belum yakin akan dipakai ulang**: tulis inline dulu, tandai dengan komentar `// TODO: promote ke component kalau muncul di layar kedua`, dan **wajib** ekstrak pada pemakaian ke-2 (lihat §2.3 "Rule of Three, Softened").

#### ❌ Anti-pattern konkret yang harus dihindari

**Duplicate primitive inline:**
```kotlin
// LoginScreen.kt
OutlinedTextField(
    value = state.email,
    onValueChange = { ... },
    isError = state.emailError != null,
    supportingText = { state.emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
    shape = RoundedCornerShape(12.dp),  // ← hardcoded, dan bakal muncul lagi di RegisterScreen
    // ...12 baris lagi
)

// RegisterScreen.kt — pola yang sama, di-copy paste
OutlinedTextField(
    value = state.email,
    onValueChange = { ... },
    // ...12 baris identik
)
```
**✅ BENAR:**
```kotlin
// ui/components/AppTextField.kt — didefinisikan sekali
@Composable
fun AppTextField(value: String, onValueChange: (String) -> Unit, label: String, errorMessage: String? = null, ...) { ... }

// LoginScreen.kt
AppTextField(value = state.email, onValueChange = { ... }, label = "Email", errorMessage = state.emailError)

// RegisterScreen.kt
AppTextField(value = state.email, onValueChange = { ... }, label = "Email", errorMessage = state.emailError)
```

**Duplicate section scaffolding:**
```swift
// LoginView.swift
VStack(spacing: 12) {
    ZStack { Circle().fill(...); Image(systemName: "lock.shield.fill")... }
    Text("Selamat Datang").font(.title2.weight(.bold))
    Text("Masuk ke akun...").font(.subheadline).foregroundColor(.secondary)
}
// RegisterView.swift — sama, hanya beda ikon & teks
```
**✅ BENAR:** Buat `AppHeaderIcon(systemImage:title:subtitle:)`, pakai di kedua tempat.

#### Tanda-tanda kamu sedang menduplikat (STOP dan promosikan)

- Kamu meng-copy-paste blok Compose/SwiftUI dari file lain ke file yang sedang kamu edit.
- Kamu sedang menulis kombinasi `Modifier` chain yang persis kamu tulis di layar lain.
- Kamu mengulang parameter set yang sama (`shape = RoundedCornerShape(14.dp)`, `height = 52.dp`, dst.) di 2 tempat.
- Kamu meng-import `OutlinedTextField` / `TextField` / `SecureField` / `Button` di file `*Screen.kt` fitur — kemungkinan besar seharusnya lewat wrapper `App*` yang sudah ada.

#### Perkecualian

Widget elementer berikut boleh dipakai langsung di feature screen (mereka **adalah** primitif akhir):
- Compose: `Text`, `Surface`, `Column`, `Row`, `Spacer`, `Box`, `TextButton` (untuk plain text link), `Checkbox`, `Card`/`CardDefaults` (bila layout struktur unik).
- SwiftUI: `Text`, `VStack`, `HStack`, `ZStack`, `Image`, `Spacer`, `Group`.

Semua bentuk **field input**, **button primer/sekunder aksi**, **banner**, **success card**, **header block** — WAJIB lewat komponen.

---

## 🟡 BAGIAN 2: Component API Design

### 2.1 Urutan Parameter (Android / Compose)

```
required data → required callbacks → modifier: Modifier = Modifier → optional config → composable slots
```

`modifier: Modifier = Modifier` **WAJIB** berada di antara parameter wajib dan opsional. Ini konvensi standar Compose — memungkinkan caller memasang `.padding()` fluently.

**❌ SALAH:**
```kotlin
fun AppPrimaryButton(
    text: String,
    isLoading: Boolean = false,   // config sebelum modifier
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
)
```

**✅ BENAR:**
```kotlin
fun AppPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
)
```

### 2.2 Nullable Optional daripada Boolean Twin

**❌ SALAH:** `AppTextField(showError: Boolean = false, error: String = "")` — dua parameter yang harus konsisten = footgun.
**✅ BENAR:** `AppTextField(errorMessage: String? = null)` — satu source of truth.

### 2.3 Rule of Three, Softened

- **Jangan** buat component pada pemakaian pertama.
- **Ekstrak** pada pemakaian ke-2 hanya kalau kamu sudah bisa membayangkan bentuk API-nya.
- **Wajib** ekstrak pada pemakaian ke-3.

Kalau ragu, biarkan duplikat dulu. Abstraksi prematur lebih mahal daripada duplikasi jinak.

### 2.4 Slot untuk Fleksibilitas Struktural, Param untuk Nilai

Kalau butuh 5 variasi visual dari komponen yang sama, pilih **slot API** (composable arg) di atas **enum config**.

```kotlin
// ❌ Enum config yang inflasi terus-menerus
fun AppCard(variant: CardVariant, hasIcon: Boolean, badge: String?)

// ✅ Slot API
fun AppCard(
    header: @Composable () -> Unit,
    body: @Composable () -> Unit,
    footer: (@Composable () -> Unit)? = null,
)
```

### 2.5 Zero DI di Leaf Component

`AppTextField` / `AppPrimaryButton` / semua leaf **DILARANG** memanggil `koinInject()`, `koinViewModel()`, `LocalContext.current` untuk hal non-visual. Terima semua data + callback dari caller.

---

## 🟢 BAGIAN 3: Testability & Preview

### 3.1 Setiap Component Punya @Preview / #Preview

Setiap file component **HARUS** menyertakan minimal satu `@Preview` (Android) atau `#Preview` (iOS) untuk state default. Bila component punya state variant (loading, error, success), tambahkan preview per variant.

### 3.2 Route Testability lewat Parameter Default

```kotlin
@Composable
fun LoginRoute(
    viewModel: LoginViewModel = koinViewModel(),  // parameter default
    ...
)
```
Test bisa: `LoginRoute(viewModel = FakeLoginViewModel())`.

Bukan:
```kotlin
@Composable
fun LoginRoute(...) {
    val vm: LoginViewModel = koinViewModel()  // ← tidak bisa di-override
    ...
}
```

### 3.3 Screen Preview Pakai Fake Store

Preview di file `FooRoute.kt` **memanggil `FooScreen(store = previewFooStore())`**, BUKAN `FooRoute()`. Alasan: `@Preview` tidak punya `ViewModelStoreOwner`/Koin container.

`previewFooStore()` di preview scaffolding wajib memakai:
- Fake data source (implementasi in-file `PreviewFooDataSource`).
- `CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)` supaya submit path bisa mencapai state terminal secara sinkron.

---

## 🔵 BAGIAN 4: Iteration Discipline

### 4.1 Add Params Only When ≥2 Call Sites Need Them

Jangan tambahkan parameter ke component karena "mungkin nanti berguna". Tambahkan hanya ketika **dua call site berbeda** benar-benar memerlukannya. Setiap parameter adalah API publik → biaya maintenance seumur hidup.

### 4.2 Naming

- Android component: `App{Semantic}` (mis. `AppPrimaryButton`, `AppErrorBanner`). Kata "App" menandai bahwa ini bagian dari design system aplikasi (bukan Material default).
- iOS component: `App{Semantic}` juga (mis. `AppPrimaryButton`, `AppTextField`).
- Route composable Android: `{Feature}Route` (mis. `LoginRoute`).
- Screen composable Android: `{Feature}Screen` (mis. `LoginScreen`).
- iOS view: `{Feature}View` (mis. `LoginView`).

### 4.3 Never Duplicate a Primitive

Jika terlihat pola `OutlinedTextField(...)` mentah di kode fitur, itu **tanda bahwa harus dipanggil lewat `AppTextField`**. Kalau AppTextField belum cukup fleksibel, perbaiki AppTextField dulu, jangan copy-paste OutlinedTextField.

---

## 📋 Checklist Sebelum Commit

- [ ] **Tidak ada duplikasi UI antar file fitur.** Cek dengan grep sederhana: pola `OutlinedTextField` / `SecureField` / `TextField(` di `<feature>/*Screen.kt` atau `*View.swift` → hampir selalu tanda pelanggaran §1.4.
- [ ] Setiap pola visual yang muncul di ≥2 layar sudah dipromosikan ke `ui/components/` / `UI/Components/`.
- [ ] Tidak ada hardcoded `.dp` / `.sp` / `CGFloat` di luar `ui/theme/`.
- [ ] Component leaf tidak memanggil DI/nav API.
- [ ] Route memakai `viewModel = koinViewModel()` sebagai parameter default.
- [ ] Screen menerima `store` sebagai parameter wajib.
- [ ] Ada minimal 1 preview per file component & per Route.
- [ ] Preview memakai fake data source, bukan production Koin/HttpClient.
- [ ] Modifier position mengikuti konvensi (setelah required, sebelum optional).
- [ ] Tidak ada `remember { mutableStateOf(...) }` untuk data yang seharusnya di store.
