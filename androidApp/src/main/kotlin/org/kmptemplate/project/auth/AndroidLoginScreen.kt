package org.kmptemplate.project.auth

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.kmptemplate.project.auth.data.AuthDataSource
import org.kmptemplate.project.auth.data.AuthRepositoryImpl
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

// region Preview scaffolding

private class PreviewLoginAuthDataSource(
    private val shouldSucceed: Boolean = true
) : AuthDataSource {
    override suspend fun authenticate(email: String, password: String): Result<User> =
        if (shouldSucceed) Result.success(
            User(id = "usr_preview", name = "Preview User", email = email, token = "preview_token")
        ) else Result.failure(IllegalArgumentException("Kredensial tidak valid (preview)."))

    override suspend fun register(name: String, email: String, password: String): Result<User> =
        Result.success(
            User(id = "usr_preview", name = name, email = email, token = "preview_token")
        )
}

private fun previewLoginStore(shouldSucceed: Boolean = true): LoginStore = LoginStore(
    repository = AuthRepositoryImpl(PreviewLoginAuthDataSource(shouldSucceed)),
    scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
)

// endregion

@Preview(name = "Login · Empty", showBackground = true, showSystemUi = true)
@Composable
private fun AndroidLoginScreenEmptyPreview() {
    val store = remember { previewLoginStore() }
    LoginScreen(store = store)
}

@Preview(name = "Login · Filled", showBackground = true, showSystemUi = true)
@Composable
private fun AndroidLoginScreenFilledPreview() {
    val store = remember {
        previewLoginStore().apply {
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
        previewLoginStore().apply {
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
        previewLoginStore(shouldSucceed = true).apply {
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
    val store = remember { previewLoginStore() }
    LoginScreen(store = store)
}
