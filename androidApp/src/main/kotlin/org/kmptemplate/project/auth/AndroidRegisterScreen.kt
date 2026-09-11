package org.kmptemplate.project.auth

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import org.kmptemplate.project.auth.model.User
import org.kmptemplate.project.auth.mvi.RegisterIntent
import org.kmptemplate.project.auth.mvi.RegisterStore

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

@Preview(name = "Register · Empty", showBackground = true, showSystemUi = true)
@Composable
private fun AndroidRegisterScreenEmptyPreview() {
    AndroidRegisterScreen()
}

@Preview(name = "Register · Filled", showBackground = true, showSystemUi = true)
@Composable
private fun AndroidRegisterScreenFilledPreview() {
    val store = remember {
        RegisterStore().apply {
            dispatch(RegisterIntent.NameChanged("Budi Santoso"))
            dispatch(RegisterIntent.EmailChanged("budi@kmptemplate.org"))
            dispatch(RegisterIntent.PasswordChanged("Password123"))
            dispatch(RegisterIntent.ConfirmPasswordChanged("Password123"))
            dispatch(RegisterIntent.ToggleTermsAccepted)
        }
    }
    RegisterScreen(store = store)
}

@Preview(name = "Register · Validation Error", showBackground = true, showSystemUi = true)
@Composable
private fun AndroidRegisterScreenErrorPreview() {
    val store = remember {
        RegisterStore().apply {
            dispatch(RegisterIntent.NameChanged("Ab"))
            dispatch(RegisterIntent.EmailChanged("bukan-email"))
            dispatch(RegisterIntent.PasswordChanged("123"))
            dispatch(RegisterIntent.ConfirmPasswordChanged("456"))
            dispatch(RegisterIntent.SubmitRegister)
        }
    }
    RegisterScreen(store = store)
}

@Preview(name = "Register · Success", showBackground = true, showSystemUi = true)
@Composable
private fun AndroidRegisterScreenSuccessPreview() {
    val store = remember {
        RegisterStore().apply {
            dispatch(RegisterIntent.NameChanged("Budi Santoso"))
            dispatch(RegisterIntent.EmailChanged("budi.baru@kmptemplate.org"))
            dispatch(RegisterIntent.PasswordChanged("Password123"))
            dispatch(RegisterIntent.ConfirmPasswordChanged("Password123"))
            dispatch(RegisterIntent.ToggleTermsAccepted)
            dispatch(RegisterIntent.SubmitRegister)
        }
    }
    RegisterScreen(store = store)
}

@Preview(
    name = "Register · Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun AndroidRegisterScreenDarkPreview() {
    AndroidRegisterScreen()
}
