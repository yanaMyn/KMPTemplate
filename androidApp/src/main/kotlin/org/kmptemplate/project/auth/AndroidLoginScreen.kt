package org.kmptemplate.project.auth

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import org.kmptemplate.project.auth.model.User
import org.kmptemplate.project.auth.mvi.LoginIntent
import org.kmptemplate.project.auth.mvi.LoginStore

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

@Preview(name = "Login · Empty", showBackground = true, showSystemUi = true)
@Composable
private fun AndroidLoginScreenEmptyPreview() {
    AndroidLoginScreen()
}

@Preview(name = "Login · Filled", showBackground = true, showSystemUi = true)
@Composable
private fun AndroidLoginScreenFilledPreview() {
    val store = remember {
        LoginStore().apply {
            dispatch(LoginIntent.EmailChanged("admin@kmptemplate.org"))
            dispatch(LoginIntent.PasswordChanged("Password123!"))
        }
    }
    LoginScreen(store = store)
}

@Preview(name = "Login · Validation Error", showBackground = true, showSystemUi = true)
@Composable
private fun AndroidLoginScreenErrorPreview() {
    val store = remember {
        LoginStore().apply {
            dispatch(LoginIntent.EmailChanged("bukan-email"))
            dispatch(LoginIntent.PasswordChanged("123"))
            dispatch(LoginIntent.SubmitLogin)
        }
    }
    LoginScreen(store = store)
}

@Preview(name = "Login · Success", showBackground = true, showSystemUi = true)
@Composable
private fun AndroidLoginScreenSuccessPreview() {
    val store = remember {
        LoginStore().apply {
            dispatch(LoginIntent.EmailChanged("admin@kmptemplate.org"))
            dispatch(LoginIntent.PasswordChanged("Password123!"))
            dispatch(LoginIntent.SubmitLogin)
        }
    }
    LoginScreen(store = store)
}

@Preview(
    name = "Login · Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun AndroidLoginScreenDarkPreview() {
    AndroidLoginScreen()
}
