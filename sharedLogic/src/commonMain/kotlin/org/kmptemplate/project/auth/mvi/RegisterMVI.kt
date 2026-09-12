package org.kmptemplate.project.auth.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.kmptemplate.project.auth.data.AuthRepository
import org.kmptemplate.project.auth.data.AuthRepositoryImpl
import org.kmptemplate.project.auth.model.User

data class RegisterState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isTermsAccepted: Boolean = false,
    val isLoading: Boolean = false,
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val termsError: String? = null,
    val generalError: String? = null,
    val isSuccess: Boolean = false,
    val registeredUser: User? = null
) {
    val canSubmit: Boolean
        get() = name.isNotBlank() &&
                email.isNotBlank() &&
                password.isNotBlank() &&
                confirmPassword.isNotBlank() &&
                nameError == null &&
                emailError == null &&
                passwordError == null &&
                confirmPasswordError == null &&
                isTermsAccepted &&
                !isLoading
}

sealed class RegisterIntent {
    data class NameChanged(val name: String) : RegisterIntent()
    data class EmailChanged(val email: String) : RegisterIntent()
    data class PasswordChanged(val password: String) : RegisterIntent()
    data class ConfirmPasswordChanged(val confirmPassword: String) : RegisterIntent()
    data object TogglePasswordVisibility : RegisterIntent()
    data object ToggleConfirmPasswordVisibility : RegisterIntent()
    data object ToggleTermsAccepted : RegisterIntent()
    data object SubmitRegister : RegisterIntent()
    data object ClearErrors : RegisterIntent()
    data object Reset : RegisterIntent()
}

fun createRegisterStore(): RegisterStore = RegisterStore()

class RegisterStore(
    private val repository: AuthRepository = AuthRepositoryImpl(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) {
    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    private var submitJob: Job? = null

    fun dispatch(intent: RegisterIntent) {
        when (intent) {
            is RegisterIntent.NameChanged -> handleNameChanged(intent.name)
            is RegisterIntent.EmailChanged -> handleEmailChanged(intent.email)
            is RegisterIntent.PasswordChanged -> handlePasswordChanged(intent.password)
            is RegisterIntent.ConfirmPasswordChanged -> handleConfirmPasswordChanged(intent.confirmPassword)
            is RegisterIntent.TogglePasswordVisibility -> handleTogglePasswordVisibility()
            is RegisterIntent.ToggleConfirmPasswordVisibility -> handleToggleConfirmPasswordVisibility()
            is RegisterIntent.ToggleTermsAccepted -> handleToggleTermsAccepted()
            is RegisterIntent.SubmitRegister -> handleSubmitRegister()
            is RegisterIntent.ClearErrors -> handleClearErrors()
            is RegisterIntent.Reset -> handleReset()
        }
    }

    fun close() {
        scope.cancel()
    }

    private fun handleNameChanged(name: String) {
        val nameError = if (name.isNotEmpty() && name.trim().length < MIN_NAME_LENGTH) {
            "Nama minimal $MIN_NAME_LENGTH karakter"
        } else {
            null
        }
        _state.update {
            it.copy(name = name, nameError = nameError, generalError = null)
        }
    }

    private fun handleEmailChanged(email: String) {
        val emailError = if (email.isNotEmpty() && !isValidEmail(email)) {
            "Format email tidak valid (contoh: nama@kmptemplate.org)"
        } else {
            null
        }
        _state.update {
            it.copy(email = email, emailError = emailError, generalError = null)
        }
    }

    private fun handlePasswordChanged(password: String) {
        val passwordError = if (password.isNotEmpty() && password.length < MIN_PASSWORD_LENGTH) {
            "Kata sandi minimal $MIN_PASSWORD_LENGTH karakter"
        } else {
            null
        }
        _state.update {
            val confirmPasswordError = validateConfirmMatch(password, it.confirmPassword)
            it.copy(
                password = password,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
                generalError = null
            )
        }
    }

    private fun handleConfirmPasswordChanged(confirmPassword: String) {
        _state.update {
            val confirmPasswordError = validateConfirmMatch(it.password, confirmPassword)
            it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = confirmPasswordError,
                generalError = null
            )
        }
    }

    private fun handleTogglePasswordVisibility() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    private fun handleToggleConfirmPasswordVisibility() {
        _state.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    private fun handleToggleTermsAccepted() {
        _state.update {
            val newTerms = !it.isTermsAccepted
            it.copy(
                isTermsAccepted = newTerms,
                termsError = if (newTerms) null else it.termsError,
                generalError = null
            )
        }
    }

    private fun handleSubmitRegister() {
        val current = _state.value
        val name = current.name.trim()
        val email = current.email.trim()
        val password = current.password.trim()
        val confirmPassword = current.confirmPassword.trim()

        var hasError = false
        var nameErr: String? = null
        var emailErr: String? = null
        var passwordErr: String? = null
        var confirmPasswordErr: String? = null
        var termsErr: String? = null

        if (name.isBlank()) {
            nameErr = "Nama tidak boleh kosong"
            hasError = true
        } else if (name.length < MIN_NAME_LENGTH) {
            nameErr = "Nama minimal $MIN_NAME_LENGTH karakter"
            hasError = true
        }

        if (email.isBlank()) {
            emailErr = "Email tidak boleh kosong"
            hasError = true
        } else if (!isValidEmail(email)) {
            emailErr = "Format email tidak valid"
            hasError = true
        }

        if (password.isBlank()) {
            passwordErr = "Kata sandi tidak boleh kosong"
            hasError = true
        } else if (password.length < MIN_PASSWORD_LENGTH) {
            passwordErr = "Kata sandi minimal $MIN_PASSWORD_LENGTH karakter"
            hasError = true
        }

        if (confirmPassword.isBlank()) {
            confirmPasswordErr = "Konfirmasi kata sandi tidak boleh kosong"
            hasError = true
        } else if (confirmPassword != password) {
            confirmPasswordErr = "Konfirmasi kata sandi tidak sama"
            hasError = true
        }

        if (!current.isTermsAccepted) {
            termsErr = "Anda harus menyetujui Syarat & Ketentuan"
            hasError = true
        }

        if (hasError) {
            _state.update {
                it.copy(
                    nameError = nameErr,
                    emailError = emailErr,
                    passwordError = passwordErr,
                    confirmPasswordError = confirmPasswordErr,
                    termsError = termsErr,
                    generalError = "Mohon lengkapi formulir dengan benar"
                )
            }
            return
        }

        submitJob?.cancel()
        submitJob = scope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) }
            repository.register(name, email, password).fold(
                onSuccess = { user ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            registeredUser = user,
                            generalError = null
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = false,
                            generalError = error.message ?: "Gagal mendaftar. Silakan coba lagi."
                        )
                    }
                }
            )
        }
    }

    private fun handleClearErrors() {
        _state.update {
            it.copy(
                nameError = null,
                emailError = null,
                passwordError = null,
                confirmPasswordError = null,
                termsError = null,
                generalError = null
            )
        }
    }

    private fun handleReset() {
        submitJob?.cancel()
        _state.value = RegisterState()
    }

    private fun validateConfirmMatch(password: String, confirmPassword: String): String? {
        return if (confirmPassword.isNotEmpty() && confirmPassword != password) {
            "Konfirmasi kata sandi tidak sama"
        } else {
            null
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.substringAfter("@").contains(".")
    }

    private companion object {
        const val MIN_NAME_LENGTH = 3
        const val MIN_PASSWORD_LENGTH = 6
    }
}
