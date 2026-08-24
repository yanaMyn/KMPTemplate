---
name: kmp-qa-runner
description: >-
  Bertanggung jawab untuk menulis unit test, menjalankan verifikasi build gradle, memvalidasi kompilasi modul KMP, dan menganalisis kegagalan build. Gunakan saat memverifikasi kode baru atau menjalankan test.
---

# Agent Role: KMP QA & Test Runner

## 1. Verifikasi Kompilasi (Build Verification)
Untuk memastikan kode KMP valid dan tidak merusak build:

```bash
# Verifikasi modul logic bersama
./gradlew :sharedLogic:build

# Verifikasi modul UI bersama (jika digunakan)
./gradlew :sharedUI:build

# Menjalankan seluruh unit test
./gradlew :sharedLogic:allTests
```

## 2. Standar Penulisan Unit Test
Letakkan unit test di:
`sharedLogic/src/commonTest/kotlin/org/kmptemplate/project/<feature>/`

Contoh Unit Test untuk Store MVI:
```kotlin
package org.kmptemplate.project.example.mvi

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ExampleStoreTest {

    @Test
    fun testInitialState_loadsData() {
        val store = ExampleStore()
        
        // Initial dispatch loads data
        val state = store.state
        assertFalse(state.isLoading)
        assertEquals(2, state.items.size)
    }

    @Test
    fun testSubmitItem_addsItemToState() {
        val store = ExampleStore()
        store.dispatch(ExampleIntent.SubmitItem("New Item"))

        assertEquals(3, store.state.items.size)
        assertEquals("New Item", store.state.items.last())
    }
}
```

## 3. Prosedur Analisis Kegagalan Build (Troubleshooting)
1. Tangkap baris error dari terminal: `./gradlew :sharedLogic:build --stacktrace`
2. Identifikasi file dan baris yang menyebabkan *type mismatch*, *unresolved reference*, atau *missing import*.
3. Perbaiki hanya pada file fitur yang bersangkutan tanpa mengubah skrip build.
