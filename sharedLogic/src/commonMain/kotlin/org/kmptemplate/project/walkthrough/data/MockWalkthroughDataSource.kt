package org.kmptemplate.project.walkthrough.data

import kotlinx.coroutines.delay
import org.kmptemplate.project.walkthrough.model.WalkthroughItem

/**
 * Dev-only walkthrough data source. Dipakai ketika `KMPT_MOCK_WALKTHROUGH=true`.
 *
 * Mengembalikan 3 item bertema KMPTemplate, dengan latency palsu ~350ms
 * supaya perilaku loading di UI tetap terpicu.
 */
class MockWalkthroughDataSource : WalkthroughDataSource {

    override suspend fun getWalkthroughItems(): List<WalkthroughItem> {
        delay(MOCK_LATENCY_MS)
        return listOf(
            WalkthroughItem(
                id = 1,
                title = "Selamat Datang di KMP Template",
                description = "Jelajahi kehebatan Kotlin Multiplatform dengan logika bersama dan performa native di Android & iOS.",
                iconName = "sparkles"
            ),
            WalkthroughItem(
                id = 2,
                title = "Arsitektur MVI + StateFlow",
                description = "Kelola state aplikasi secara terpusat, terprediksi, dan mudah diuji dengan Model-View-Intent.",
                iconName = "layers"
            ),
            WalkthroughItem(
                id = 3,
                title = "Native UI Terbaik",
                description = "Nikmati antarmuka Jetpack Compose di Android dan SwiftUI di iOS untuk pengalaman pengguna optimal.",
                iconName = "devices"
            )
        )
    }

    private companion object {
        const val MOCK_LATENCY_MS = 350L
    }
}
