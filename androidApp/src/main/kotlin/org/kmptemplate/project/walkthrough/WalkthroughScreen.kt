package org.kmptemplate.project.walkthrough

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

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

@Preview(showBackground = true)
@Composable
fun AndroidWalkthroughScreenPreview() {
    AndroidWalkthroughScreen()
}
