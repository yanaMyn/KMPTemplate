package org.kmptemplate.project.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.kmptemplate.project.auth.model.User

/**
 * Android Native entry point for RegisterScreen.
 * Delegates to the Multiplatform RegisterScreen from sharedUI.
 */
@Composable
fun AndroidRegisterScreen(
    onRegisterSuccess: (User) -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    RegisterScreen(
        onRegisterSuccess = onRegisterSuccess,
        onNavigateToLogin = onNavigateToLogin,
        onBack = onBack
    )
}

@Preview(showBackground = true)
@Composable
fun AndroidRegisterScreenPreview() {
    AndroidRegisterScreen()
}
