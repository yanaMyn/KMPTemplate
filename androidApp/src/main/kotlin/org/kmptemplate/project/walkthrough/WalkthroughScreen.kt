package org.kmptemplate.project.walkthrough

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.kmptemplate.project.walkthrough.data.WalkthroughDataSource
import org.kmptemplate.project.walkthrough.data.WalkthroughRepositoryImpl
import org.kmptemplate.project.walkthrough.model.WalkthroughItem
import org.kmptemplate.project.walkthrough.mvi.WalkthroughIntent
import org.kmptemplate.project.walkthrough.mvi.WalkthroughStore

/**
 * Android Native entry point for WalkthroughScreen.
 * Delegates to the Multiplatform WalkthroughScreen from sharedUI.
 */
@Composable
fun AndroidWalkthroughScreen(
    onFinished: () -> Unit = {}
) {
    WalkthroughScreen(
        onFinished = onFinished
    )
}

// region Preview scaffolding

private class PreviewWalkthroughDataSource : WalkthroughDataSource {
    override suspend fun getWalkthroughItems(): List<WalkthroughItem> = listOf(
        WalkthroughItem(1, "Selamat Datang", "Preview walkthrough page 1.", "sparkles"),
        WalkthroughItem(2, "Arsitektur MVI", "Preview walkthrough page 2.", "layers"),
        WalkthroughItem(3, "Native UI Terbaik", "Preview walkthrough page 3.", "devices")
    )
}

private fun previewWalkthroughStore(): WalkthroughStore = WalkthroughStore(
    repository = WalkthroughRepositoryImpl(PreviewWalkthroughDataSource()),
    scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
)

// endregion

@Preview(name = "Walkthrough · First Page", showBackground = true, showSystemUi = true)
@Composable
private fun AndroidWalkthroughScreenFirstPagePreview() {
    val store = remember { previewWalkthroughStore() }
    WalkthroughScreen(store = store)
}

@Preview(name = "Walkthrough · Middle Page", showBackground = true, showSystemUi = true)
@Composable
private fun AndroidWalkthroughScreenMiddlePagePreview() {
    val store = remember {
        previewWalkthroughStore().apply {
            dispatch(WalkthroughIntent.NextPage)
        }
    }
    WalkthroughScreen(store = store)
}

@Preview(name = "Walkthrough · Last Page", showBackground = true, showSystemUi = true)
@Composable
private fun AndroidWalkthroughScreenLastPagePreview() {
    val store = remember {
        previewWalkthroughStore().apply {
            val lastIndex = state.value.items.lastIndex.coerceAtLeast(0)
            dispatch(WalkthroughIntent.SelectPage(index = lastIndex))
        }
    }
    WalkthroughScreen(store = store)
}

@Preview(name = "Walkthrough · Completed", showBackground = true, showSystemUi = true)
@Composable
private fun AndroidWalkthroughScreenCompletedPreview() {
    val store = remember {
        previewWalkthroughStore().apply {
            dispatch(WalkthroughIntent.Complete)
        }
    }
    WalkthroughScreen(store = store)
}

@Preview(
    name = "Walkthrough · Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun AndroidWalkthroughScreenDarkPreview() {
    val store = remember { previewWalkthroughStore() }
    WalkthroughScreen(store = store)
}
