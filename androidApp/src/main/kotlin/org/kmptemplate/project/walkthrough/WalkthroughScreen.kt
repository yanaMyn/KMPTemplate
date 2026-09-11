package org.kmptemplate.project.walkthrough

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
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

@Preview(name = "Walkthrough · First Page", showBackground = true, showSystemUi = true)
@Composable
private fun AndroidWalkthroughScreenFirstPagePreview() {
    AndroidWalkthroughScreen()
}

@Preview(name = "Walkthrough · Middle Page", showBackground = true, showSystemUi = true)
@Composable
private fun AndroidWalkthroughScreenMiddlePagePreview() {
    val store = remember {
        WalkthroughStore().apply {
            dispatch(WalkthroughIntent.NextPage)
        }
    }
    WalkthroughScreen(store = store)
}

@Preview(name = "Walkthrough · Last Page", showBackground = true, showSystemUi = true)
@Composable
private fun AndroidWalkthroughScreenLastPagePreview() {
    val store = remember {
        WalkthroughStore().apply {
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
        WalkthroughStore().apply {
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
    AndroidWalkthroughScreen()
}
