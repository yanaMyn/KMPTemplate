package org.kmptemplate.project.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Design tokens untuk aplikasi.
 *
 * Prinsip:
 * - Selalu konsumsi angka dari `AppSpacing` / `AppShapes` — jangan hardcode `16.dp` di call site.
 * - Warna/typografi dilewatkan lewat `MaterialTheme` (pakai `MaterialTheme.colorScheme.primary`,
 *   dst.) supaya night-mode & dynamic color otomatis bekerja.
 */
object AppSpacing {
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 12.dp
    val lg: Dp = 16.dp
    val xl: Dp = 24.dp
    val xxl: Dp = 32.dp
}

object AppShapes {
    val cornerSmall: Dp = 10.dp
    val cornerMedium: Dp = 12.dp
    val cornerLarge: Dp = 14.dp
    val cornerXLarge: Dp = 16.dp
}

object AppSizes {
    val fieldHeight: Dp = 56.dp
    val buttonHeight: Dp = 52.dp
    val headerIcon: Dp = 80.dp
    val successIcon: Dp = 90.dp
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme(),
        content = content
    )
}
