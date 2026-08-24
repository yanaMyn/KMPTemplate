package org.kmptemplate.project.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.kmptemplate.project.auth.model.User

/**
 * Android Native entry point for LoginScreen.
 * Delegates to the Multiplatform LoginScreen from sharedUI.
 */
@Composable
fun AndroidLoginScreen(
    onLoginSuccess: (User) -> Unit = {},
    onBack: () -> Unit = {}
) {
    LoginScreen(
        onLoginSuccess = onLoginSuccess,
        onBack = onBack
    )
}

@Preview(showBackground = true)
@Composable
fun AndroidLoginScreenPreview() {
    AndroidLoginScreen()
}
