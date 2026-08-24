package org.kmptemplate.project.walkthrough.data

import org.kmptemplate.project.walkthrough.model.WalkthroughItem

interface WalkthroughDataSource {
    fun getWalkthroughItems(): List<WalkthroughItem>
}

class DefaultWalkthroughDataSource : WalkthroughDataSource {
    override fun getWalkthroughItems(): List<WalkthroughItem> {
        return listOf(
            WalkthroughItem(
                id = 1,
                title = "Selamat Datang di KMP Template",
                description = "Jelajahi kehebatan Kotlin Multiplatform dengan logika bersama dan performa native di Android & iOS.",
                iconName = "sparkles"
            ),
            WalkthroughItem(
                id = 2,
                title = "Arsitektur MVI yang Teruji",
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
}
