package org.kmptemplate.project.auth.mvi

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

class RegisterStore(
    private val repository: AuthRepository = AuthRepositoryImpl()
) {
    private var _state = RegisterState()
    val state: RegisterState
        get() = _state

    private val listeners = mutableListOf<(RegisterState) -> Unit>()

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

    private fun updateState(newState: RegisterState) {
        _state = newState
        notifyListeners()
    }

    private fun notifyListeners() {
        val currentState = _state
        listeners.forEach { it(currentState) }
    }

    private fun handleNameChanged(name: String) {
        val nameError = if (name.isNotEmpty() && name.trim().length < MIN_NAME_LENGTH) {
            "Nama minimal $MIN_NAME_LENGTH karakter"
        } else {
            null
        }
        updateState(
            _state.copy(
                name = name,
                nameError = nameError,
                generalError = null
            )
        )
    }

    private fun handleEmailChanged(email: String) {
        val emailError = if (email.isNotEmpty() && !isValidEmail(email)) {
            "Format email tidak valid (contoh: nama@kmptemplate.org)"
        } else {
            null
        }
        updateState(
            _state.copy(
                email = email,
                emailError = emailError,
                generalError = null
            )
        )
    }

    private fun handlePasswordChanged(password: String) {
        val passwordError = if (password.isNotEmpty() && password.length < MIN_PASSWORD_LENGTH) {
            "Kata sandi minimal $MIN_PASSWORD_LENGTH karakter"
        } else {
            null
        }
        // Konfirmasi divalidasi ulang agar error "tidak sama" hilang otomatis
        // ketika pengguna memperbaiki kata sandi utamanya.
        val confirmPasswordError = validateConfirmMatch(password, _state.confirmPassword)
        updateState(
            _state.copy(
                password = password,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
                generalError = null
            )
        )
    }

    private fun handleConfirmPasswordChanged(confirmPassword: String) {
        val confirmPasswordError = validateConfirmMatch(_state.password, confirmPassword)
        updateState(
            _state.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = confirmPasswordError,
                generalError = null
            )
        )
    }

    private fun handleTogglePasswordVisibility() {
        updateState(_state.copy(isPasswordVisible = !_state.isPasswordVisible))
    }

    private fun handleToggleConfirmPasswordVisibility() {
        updateState(_state.copy(isConfirmPasswordVisible = !_state.isConfirmPasswordVisible))
    }

    private fun handleToggleTermsAccepted() {
        val isTermsAccepted = !_state.isTermsAccepted
        updateState(
            _state.copy(
                isTermsAccepted = isTermsAccepted,
                termsError = if (isTermsAccepted) null else _state.termsError,
                generalError = null
            )
        )
    }

    private fun handleSubmitRegister() {
        val name = _state.name.trim()
        val email = _state.email.trim()
        val password = _state.password.trim()
        val confirmPassword = _state.confirmPassword.trim()

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

        if (!_state.isTermsAccepted) {
            termsErr = "Anda harus menyetujui Syarat & Ketentuan"
            hasError = true
        }

        if (hasError) {
            updateState(
                _state.copy(
                    nameError = nameErr,
                    emailError = emailErr,
                    passwordError = passwordErr,
                    confirmPasswordError = confirmPasswordErr,
                    termsError = termsErr,
                    generalError = "Mohon lengkapi formulir dengan benar"
                )
            )
            return
        }

        updateState(_state.copy(isLoading = true, generalError = null))

        val result = repository.register(name, email, password)
        result.fold(
            onSuccess = { user ->
                updateState(
                    _state.copy(
                        isLoading = false,
                        isSuccess = true,
                        registeredUser = user,
                        generalError = null
                    )
                )
            },
            onFailure = { error ->
                updateState(
                    _state.copy(
                        isLoading = false,
                        isSuccess = false,
                        generalError = error.message ?: "Gagal mendaftar. Silakan coba lagi."
                    )
                )
            }
        )
    }

    private fun handleClearErrors() {
        updateState(
            _state.copy(
                nameError = null,
                emailError = null,
                passwordError = null,
                confirmPasswordError = null,
                termsError = null,
                generalError = null
            )
        )
    }

    private fun handleReset() {
        updateState(RegisterState())
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

    /**
     * Subscribe to state updates. Returns an unsubscribe function.
     * Invokes listener immediately with the current state.
     */
    fun subscribe(listener: (RegisterState) -> Unit): () -> Unit {
        listeners.add(listener)
        listener(_state)
        return {
            listeners.remove(listener)
        }
    }

    private companion object {
        const val MIN_NAME_LENGTH = 3
        const val MIN_PASSWORD_LENGTH = 6
    }
}
