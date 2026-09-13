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
import org.kmptemplate.project.auth.mvi.RegisterIntent
import org.kmptemplate.project.auth.mvi.RegisterStore

@Composable
fun RegisterRoute(
    viewModel: RegisterViewModel = koinViewModel(),
    onRegisterSuccess: (User) -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    RegisterScreen(
        store = viewModel.store,
        onRegisterSuccess = onRegisterSuccess,
        onNavigateToLogin = onNavigateToLogin,
        onBack = onBack
    )
}

// region Preview scaffolding

private class PreviewRegisterAuthDataSource(
    private val shouldSucceed: Boolean = true
) : AuthDataSource {
    override suspend fun authenticate(email: String, password: String): Result<User> =
        Result.failure(IllegalStateException("Autentikasi tidak digunakan di preview register."))

    override suspend fun register(name: String, email: String, password: String): Result<User> =
        if (shouldSucceed) Result.success(
            User(id = "usr_preview", name = name, email = email, token = "preview_token")
        ) else Result.failure(IllegalArgumentException("Email sudah terdaftar (preview)."))
}

private fun previewRegisterStore(shouldSucceed: Boolean = true): RegisterStore = RegisterStore(
    repository = AuthRepositoryImpl(PreviewRegisterAuthDataSource(shouldSucceed)),
    scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
)

// endregion

@Preview(name = "Register · Empty", showBackground = true, showSystemUi = true)
@Composable
private fun RegisterEmptyPreview() {
    val store = remember { previewRegisterStore() }
    RegisterScreen(store = store)
}

@Preview(name = "Register · Filled", showBackground = true, showSystemUi = true)
@Composable
private fun RegisterFilledPreview() {
    val store = remember {
        previewRegisterStore().apply {
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
private fun RegisterErrorPreview() {
    val store = remember {
        previewRegisterStore().apply {
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
private fun RegisterSuccessPreview() {
    val store = remember {
        previewRegisterStore(shouldSucceed = true).apply {
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
private fun RegisterDarkPreview() {
    val store = remember { previewRegisterStore() }
    RegisterScreen(store = store)
}
