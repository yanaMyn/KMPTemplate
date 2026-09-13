package org.kmptemplate.project.auth

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.androidx.compose.koinViewModel
import org.kmptemplate.project.auth.data.AuthDataSource
import org.kmptemplate.project.auth.data.AuthRepositoryImpl
import org.kmptemplate.project.auth.model.User
import org.kmptemplate.project.auth.mvi.LoginIntent
import org.kmptemplate.project.auth.mvi.LoginStore

/**
 * Stateful "route" untuk Login — pintu masuk yang ditunjuk oleh navigator (App / NavHost).
 *
 * `viewModel` di-declare sebagai parameter dengan default `koinViewModel()` supaya:
 * 1. UI test dapat mengoper ViewModel palsu (`LoginRoute(viewModel = fake)`),
 * 2. Signature langsung menampilkan dependency graph route ini.
 *
 * Store bertahan config change karena dimiliki `LoginViewModel` (yang di-scope ke
 * `ViewModelStore` host); `LoginViewModel.onCleared()` menutup CoroutineScope store.
 */
@Composable
fun LoginRoute(
    viewModel: LoginViewModel = koinViewModel(),
    onLoginSuccess: (User) -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    LoginScreen(
        store = viewModel.store,
        onLoginSuccess = onLoginSuccess,
        onNavigateToRegister = onNavigateToRegister,
        onBack = onBack
    )
}

// region Preview scaffolding
// Preview mem-bypass ViewModel/Koin dan langsung memberi `LoginScreen` sebuah
// store yang diseed pakai fake data source — ini yang bikin @Preview bisa render
// tanpa `Application` / `ViewModelStoreOwner` / Koin container.

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
private fun LoginEmptyPreview() {
    val store = remember { previewLoginStore() }
    LoginScreen(store = store)
}

@Preview(name = "Login · Filled", showBackground = true, showSystemUi = true)
@Composable
private fun LoginFilledPreview() {
    val store = remember {
        previewLoginStore().apply {
            dispatch(LoginIntent.EmailChanged("Sincere@april.biz"))
            dispatch(LoginIntent.PasswordChanged("Password123!"))
        }
    }
    LoginScreen(store = store)
}

@Preview(name = "Login · Validation Error", showBackground = true, showSystemUi = true)
@Composable
private fun LoginErrorPreview() {
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
private fun LoginSuccessPreview() {
    val store = remember {
        previewLoginStore(shouldSucceed = true).apply {
            dispatch(LoginIntent.EmailChanged("Sincere@april.biz"))
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
private fun LoginDarkPreview() {
    val store = remember { previewLoginStore() }
    LoginScreen(store = store)
}
