package org.kmptemplate.project.auth

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.kmptemplate.project.auth.model.User
import org.kmptemplate.project.auth.mvi.LoginIntent
import org.kmptemplate.project.auth.mvi.LoginState
import org.kmptemplate.project.auth.mvi.LoginStore
import org.kmptemplate.project.ui.components.AppErrorBanner
import org.kmptemplate.project.ui.components.AppHeaderIcon
import org.kmptemplate.project.ui.components.AppPasswordField
import org.kmptemplate.project.ui.components.AppPrimaryButton
import org.kmptemplate.project.ui.components.AppSuccessCard
import org.kmptemplate.project.ui.components.AppTextField
import org.kmptemplate.project.ui.theme.AppShapes
import org.kmptemplate.project.ui.theme.AppSpacing
import androidx.compose.runtime.collectAsState

/**
 * Layar Login (stateless secara identitas store).
 *
 * Pemanggil (Android ViewModel / iOS `@StateObject` / preview scaffolding) bertanggung
 * jawab atas siklus hidup `store` — screen tidak memanggil `store.close()`.
 */
@Composable
fun LoginScreen(
    store: LoginStore,
    onLoginSuccess: (User) -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val state by store.state.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (state.isSuccess && state.loggedInUser != null) {
            val user = state.loggedInUser!!
            AppSuccessCard(
                emoji = "🎉",
                title = "Login Berhasil!",
                subtitle = "Selamat datang kembali, ${user.name}!",
                caption = "Email: ${user.email}",
                primaryText = "Lanjutkan ke Aplikasi",
                onPrimary = { onLoginSuccess(user) },
                secondaryText = "Keluar (Logout)",
                onSecondary = { store.dispatch(LoginIntent.Reset) },
            )
        } else {
            LoginFormView(
                state = state,
                onIntent = { store.dispatch(it) },
                onNavigateToRegister = onNavigateToRegister,
                onBack = onBack
            )
        }
    }
}

@Composable
private fun LoginFormView(
    state: LoginState,
    onIntent: (LoginIntent) -> Unit,
    onNavigateToRegister: () -> Unit,
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
            emoji = "🔐",
            title = "Selamat Datang Kembali",
            subtitle = "Masuk ke akun KMPTemplate Anda"
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl + AppSpacing.xs))

        AppErrorBanner(message = state.generalError)

        AppTextField(
            value = state.email,
            onValueChange = { onIntent(LoginIntent.EmailChanged(it)) },
            label = "Email",
            placeholder = "contoh: admin@kmptemplate.org",
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
            onValueChange = { onIntent(LoginIntent.PasswordChanged(it)) },
            label = "Kata Sandi",
            placeholder = "Minimal 6 karakter",
            errorMessage = state.passwordError,
            isVisible = state.isPasswordVisible,
            onToggleVisibility = { onIntent(LoginIntent.TogglePasswordVisibility) },
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onIntent(LoginIntent.SubmitLogin)
                }
            ),
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))

        AppPrimaryButton(
            text = "Masuk",
            onClick = {
                focusManager.clearFocus()
                onIntent(LoginIntent.SubmitLogin)
            },
            isLoading = state.isLoading,
        )

        Spacer(modifier = Modifier.height(AppSpacing.md))

        TextButton(onClick = onNavigateToRegister) {
            Text(
                text = "Belum punya akun? Daftar",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(AppSpacing.lg + AppSpacing.xs))

        DemoCredentialsCard(onIntent = onIntent)
    }
}

@Composable
private fun DemoCredentialsCard(onIntent: (LoginIntent) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(AppShapes.cornerLarge),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Akun Demo Cepat (Klik untuk isi)",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
            ) {
                FilledTonalButton(
                    onClick = {
                        onIntent(LoginIntent.EmailChanged("Sincere@april.biz"))
                        onIntent(LoginIntent.PasswordChanged("Password123!"))
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(AppShapes.cornerSmall)
                ) {
                    Text("Leanne Graham", fontSize = 12.sp)
                }
                FilledTonalButton(
                    onClick = {
                        onIntent(LoginIntent.EmailChanged("Shanna@melissa.tv"))
                        onIntent(LoginIntent.PasswordChanged("Password123!"))
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(AppShapes.cornerSmall)
                ) {
                    Text("Ervin Howell", fontSize = 12.sp)
                }
            }
        }
    }
}
