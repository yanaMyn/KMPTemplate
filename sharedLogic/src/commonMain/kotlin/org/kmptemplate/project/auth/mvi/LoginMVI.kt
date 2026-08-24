package org.kmptemplate.project.auth.mvi

import org.kmptemplate.project.auth.data.AuthRepository
import org.kmptemplate.project.auth.data.AuthRepositoryImpl
import org.kmptemplate.project.auth.model.User

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null,
    val isSuccess: Boolean = false,
    val loggedInUser: User? = null
) {
    val canSubmit: Boolean
        get() = email.isNotBlank() &&
                password.isNotBlank() &&
                emailError == null &&
                passwordError == null &&
                !isLoading
}

sealed class LoginIntent {
    data class EmailChanged(val email: String) : LoginIntent()
    data class PasswordChanged(val password: String) : LoginIntent()
    data object TogglePasswordVisibility : LoginIntent()
    data object SubmitLogin : LoginIntent()
    data object ClearErrors : LoginIntent()
    data object Reset : LoginIntent()
}

class LoginStore(
    private val repository: AuthRepository = AuthRepositoryImpl()
) {
    private var _state = LoginState()
    val state: LoginState
        get() = _state

    private val listeners = mutableListOf<(LoginState) -> Unit>()

    fun dispatch(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EmailChanged -> handleEmailChanged(intent.email)
            is LoginIntent.PasswordChanged -> handlePasswordChanged(intent.password)
            is LoginIntent.TogglePasswordVisibility -> handleTogglePasswordVisibility()
            is LoginIntent.SubmitLogin -> handleSubmitLogin()
            is LoginIntent.ClearErrors -> handleClearErrors()
            is LoginIntent.Reset -> handleReset()
        }
    }

    private fun updateState(newState: LoginState) {
        _state = newState
        notifyListeners()
    }

    private fun notifyListeners() {
        val currentState = _state
        listeners.forEach { it(currentState) }
    }

    private fun handleEmailChanged(email: String) {
        val emailError = if (email.isNotEmpty() && !isValidEmail(email)) {
            "Format email tidak valid (contoh: user@kmptemplate.org)"
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
        val passwordError = if (password.isNotEmpty() && password.length < 6) {
            "Kata sandi minimal 6 karakter"
        } else {
            null
        }
        updateState(
            _state.copy(
                password = password,
                passwordError = passwordError,
                generalError = null
            )
        )
    }

    private fun handleTogglePasswordVisibility() {
        updateState(_state.copy(isPasswordVisible = !_state.isPasswordVisible))
    }

    private fun handleSubmitLogin() {
        val email = _state.email.trim()
        val password = _state.password.trim()

        var hasError = false
        var emailErr: String? = null
        var passwordErr: String? = null

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
        } else if (password.length < 6) {
            passwordErr = "Kata sandi minimal 6 karakter"
            hasError = true
        }

        if (hasError) {
            updateState(
                _state.copy(
                    emailError = emailErr,
                    passwordError = passwordErr,
                    generalError = "Mohon lengkapi formulir dengan benar"
                )
            )
            return
        }

        updateState(_state.copy(isLoading = true, generalError = null))

        val result = repository.login(email, password)
        result.fold(
            onSuccess = { user ->
                updateState(
                    _state.copy(
                        isLoading = false,
                        isSuccess = true,
                        loggedInUser = user,
                        generalError = null
                    )
                )
            },
            onFailure = { error ->
                updateState(
                    _state.copy(
                        isLoading = false,
                        isSuccess = false,
                        generalError = error.message ?: "Gagal masuk. Silakan coba lagi."
                    )
                )
            }
        )
    }

    private fun handleClearErrors() {
        updateState(
            _state.copy(
                emailError = null,
                passwordError = null,
                generalError = null
            )
        )
    }

    private fun handleReset() {
        updateState(LoginState())
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.substringAfter("@").contains(".")
    }

    /**
     * Subscribe to state updates. Returns an unsubscribe function.
     * Invokes listener immediately with the current state.
     */
    fun subscribe(listener: (LoginState) -> Unit): () -> Unit {
        listeners.add(listener)
        listener(_state)
        return {
            listeners.remove(listener)
        }
    }
}
