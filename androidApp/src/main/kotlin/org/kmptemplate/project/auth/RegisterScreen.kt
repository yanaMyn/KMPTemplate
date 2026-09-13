package org.kmptemplate.project.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import org.kmptemplate.project.auth.model.User
import org.kmptemplate.project.auth.mvi.RegisterIntent
import org.kmptemplate.project.auth.mvi.RegisterState
import org.kmptemplate.project.auth.mvi.RegisterStore
import org.kmptemplate.project.ui.components.AppErrorBanner
import org.kmptemplate.project.ui.components.AppHeaderIcon
import org.kmptemplate.project.ui.components.AppPasswordField
import org.kmptemplate.project.ui.components.AppPrimaryButton
import org.kmptemplate.project.ui.components.AppSuccessCard
import org.kmptemplate.project.ui.components.AppTextField
import org.kmptemplate.project.ui.theme.AppSpacing

@Composable
fun RegisterScreen(
    store: RegisterStore,
    onRegisterSuccess: (User) -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val state by store.state.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (state.isSuccess && state.registeredUser != null) {
            val user = state.registeredUser!!
            AppSuccessCard(
                emoji = "🎉",
                title = "Pendaftaran Berhasil!",
                subtitle = "Selamat datang, ${user.name}!",
                caption = "Email: ${user.email}",
                primaryText = "Lanjutkan ke Aplikasi",
                onPrimary = { onRegisterSuccess(user) },
                secondaryText = "Daftar Akun Lain",
                onSecondary = { store.dispatch(RegisterIntent.Reset) },
            )
        } else {
            RegisterFormView(
                state = state,
                onIntent = { store.dispatch(it) },
                onNavigateToLogin = onNavigateToLogin,
                onBack = onBack
            )
        }
    }
}

@Composable
private fun RegisterFormView(
    state: RegisterState,
    onIntent: (RegisterIntent) -> Unit,
    onNavigateToLogin: () -> Unit,
    onBack: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .verticalScroll(scrollState)
            .padding(AppSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            TextButton(onClick = onBack) { Text("← Kembali") }
        }

        Spacer(modifier = Modifier.height(AppSpacing.lg))

        AppHeaderIcon(
            emoji = "📝",
            title = "Buat Akun Baru",
            subtitle = "Daftar untuk mulai menggunakan KMPTemplate"
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl + AppSpacing.xs))

        AppErrorBanner(message = state.generalError)

        AppTextField(
            value = state.name,
            onValueChange = { onIntent(RegisterIntent.NameChanged(it)) },
            label = "Nama Lengkap",
            placeholder = "contoh: Budi Santoso",
            errorMessage = state.nameError,
            imeAction = ImeAction.Next,
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
        )

        Spacer(modifier = Modifier.height(AppSpacing.md))

        AppTextField(
            value = state.email,
            onValueChange = { onIntent(RegisterIntent.EmailChanged(it)) },
            label = "Email",
            placeholder = "contoh: nama@kmptemplate.org",
            errorMessage = state.emailError,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
        )

        Spacer(modifier = Modifier.height(AppSpacing.md))

        AppPasswordField(
            value = state.password,
            onValueChange = { onIntent(RegisterIntent.PasswordChanged(it)) },
            label = "Kata Sandi",
            placeholder = "Minimal 6 karakter",
            errorMessage = state.passwordError,
            isVisible = state.isPasswordVisible,
            onToggleVisibility = { onIntent(RegisterIntent.TogglePasswordVisibility) },
            imeAction = ImeAction.Next,
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
        )

        Spacer(modifier = Modifier.height(AppSpacing.md))

        AppPasswordField(
            value = state.confirmPassword,
            onValueChange = { onIntent(RegisterIntent.ConfirmPasswordChanged(it)) },
            label = "Konfirmasi Kata Sandi",
            placeholder = "Ulangi kata sandi",
            errorMessage = state.confirmPasswordError,
            isVisible = state.isConfirmPasswordVisible,
            onToggleVisibility = { onIntent(RegisterIntent.ToggleConfirmPasswordVisibility) },
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onIntent(RegisterIntent.SubmitRegister)
                }
            ),
        )

        Spacer(modifier = Modifier.height(AppSpacing.sm))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = state.isTermsAccepted,
                onCheckedChange = { onIntent(RegisterIntent.ToggleTermsAccepted) }
            )
            Text(
                text = "Saya menyetujui Syarat & Ketentuan",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        AnimatedVisibility(visible = state.termsError != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = state.termsError.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.xl))

        AppPrimaryButton(
            text = "Daftar",
            onClick = {
                focusManager.clearFocus()
                onIntent(RegisterIntent.SubmitRegister)
            },
            isLoading = state.isLoading,
        )

        Spacer(modifier = Modifier.height(AppSpacing.md))

        TextButton(onClick = onNavigateToLogin) {
            Text(
                text = "Sudah punya akun? Masuk",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
