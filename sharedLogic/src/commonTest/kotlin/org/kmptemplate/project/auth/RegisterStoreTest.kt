package org.kmptemplate.project.auth

import org.kmptemplate.project.auth.data.AuthDataSource
import org.kmptemplate.project.auth.data.AuthRepositoryImpl
import org.kmptemplate.project.auth.model.User
import org.kmptemplate.project.auth.mvi.RegisterIntent
import org.kmptemplate.project.auth.mvi.RegisterState
import org.kmptemplate.project.auth.mvi.RegisterStore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RegisterStoreTest {

    private class FakeAuthDataSource : AuthDataSource {
        override fun authenticate(email: String, password: String): Result<User> {
            return Result.failure(
                IllegalStateException("Autentikasi tidak digunakan pada RegisterStoreTest")
            )
        }

        override fun register(name: String, email: String, password: String): Result<User> {
            if (email == TAKEN_EMAIL) {
                return Result.failure(
                    IllegalArgumentException("Email sudah terdaftar. Silakan gunakan email lain.")
                )
            }
            return Result.success(
                User(
                    id = "usr_reg_test",
                    name = name,
                    email = email,
                    token = "mock_token_register_test"
                )
            )
        }
    }

    private fun createStore(): RegisterStore {
        val repo = AuthRepositoryImpl(FakeAuthDataSource())
        return RegisterStore(repository = repo)
    }

    /** Mengisi seluruh field dengan data valid dan menyetujui syarat & ketentuan. */
    private fun RegisterStore.fillValidForm(email: String = "baru@kmptemplate.org") {
        dispatch(RegisterIntent.NameChanged("Budi Santoso"))
        dispatch(RegisterIntent.EmailChanged(email))
        dispatch(RegisterIntent.PasswordChanged("Secret123"))
        dispatch(RegisterIntent.ConfirmPasswordChanged("Secret123"))
        dispatch(RegisterIntent.ToggleTermsAccepted)
    }

    @Test
    fun testInitialState() {
        val store = createStore()
        val state = store.state

        assertEquals("", state.name)
        assertEquals("", state.email)
        assertEquals("", state.password)
        assertEquals("", state.confirmPassword)
        assertFalse(state.isPasswordVisible)
        assertFalse(state.isConfirmPasswordVisible)
        assertFalse(state.isTermsAccepted)
        assertFalse(state.isLoading)
        assertFalse(state.isSuccess)
        assertNull(state.nameError)
        assertNull(state.emailError)
        assertNull(state.passwordError)
        assertNull(state.confirmPasswordError)
        assertNull(state.termsError)
        assertNull(state.generalError)
        assertNull(state.registeredUser)
        assertFalse(state.canSubmit)
    }

    @Test
    fun testNameValidation() {
        val store = createStore()

        store.dispatch(RegisterIntent.NameChanged("Bu"))
        assertEquals("Bu", store.state.name)
        assertNotNull(store.state.nameError)
        assertFalse(store.state.canSubmit)

        store.dispatch(RegisterIntent.NameChanged("Budi"))
        assertEquals("Budi", store.state.name)
        assertNull(store.state.nameError)
    }

    @Test
    fun testEmailValidation() {
        val store = createStore()

        store.dispatch(RegisterIntent.EmailChanged("invalid-email"))
        assertEquals("invalid-email", store.state.email)
        assertNotNull(store.state.emailError)
        assertFalse(store.state.canSubmit)

        store.dispatch(RegisterIntent.EmailChanged("baru@kmptemplate.org"))
        assertEquals("baru@kmptemplate.org", store.state.email)
        assertNull(store.state.emailError)
    }

    @Test
    fun testPasswordValidation() {
        val store = createStore()

        store.dispatch(RegisterIntent.PasswordChanged("123"))
        assertEquals("123", store.state.password)
        assertNotNull(store.state.passwordError)
        assertFalse(store.state.canSubmit)

        store.dispatch(RegisterIntent.PasswordChanged("Secret123"))
        assertEquals("Secret123", store.state.password)
        assertNull(store.state.passwordError)
    }

    @Test
    fun testConfirmPasswordMismatch() {
        val store = createStore()
        store.dispatch(RegisterIntent.PasswordChanged("Secret123"))

        store.dispatch(RegisterIntent.ConfirmPasswordChanged("Secret999"))
        assertNotNull(store.state.confirmPasswordError)
        assertFalse(store.state.canSubmit)

        store.dispatch(RegisterIntent.ConfirmPasswordChanged("Secret123"))
        assertNull(store.state.confirmPasswordError)
    }

    @Test
    fun testConfirmPasswordRevalidatedWhenPasswordChanges() {
        val store = createStore()

        // Konfirmasi diisi lebih dulu, lalu password diubah sampai cocok.
        store.dispatch(RegisterIntent.PasswordChanged("Secret123"))
        store.dispatch(RegisterIntent.ConfirmPasswordChanged("Secret1234"))
        assertNotNull(store.state.confirmPasswordError)

        store.dispatch(RegisterIntent.PasswordChanged("Secret1234"))
        assertNull(store.state.confirmPasswordError)
        assertNull(store.state.passwordError)
    }

    @Test
    fun testTogglePasswordVisibility() {
        val store = createStore()
        assertFalse(store.state.isPasswordVisible)
        assertFalse(store.state.isConfirmPasswordVisible)

        store.dispatch(RegisterIntent.TogglePasswordVisibility)
        assertTrue(store.state.isPasswordVisible)
        assertFalse(store.state.isConfirmPasswordVisible)

        store.dispatch(RegisterIntent.ToggleConfirmPasswordVisibility)
        assertTrue(store.state.isConfirmPasswordVisible)

        store.dispatch(RegisterIntent.TogglePasswordVisibility)
        store.dispatch(RegisterIntent.ToggleConfirmPasswordVisibility)
        assertFalse(store.state.isPasswordVisible)
        assertFalse(store.state.isConfirmPasswordVisible)
    }

    @Test
    fun testToggleTermsAccepted() {
        val store = createStore()
        assertFalse(store.state.isTermsAccepted)

        store.dispatch(RegisterIntent.ToggleTermsAccepted)
        assertTrue(store.state.isTermsAccepted)

        store.dispatch(RegisterIntent.ToggleTermsAccepted)
        assertFalse(store.state.isTermsAccepted)
    }

    @Test
    fun testSubmitWithEmptyFieldsShowsAllErrors() {
        val store = createStore()
        store.dispatch(RegisterIntent.SubmitRegister)

        val state = store.state
        assertFalse(state.isSuccess)
        assertNotNull(state.nameError)
        assertNotNull(state.emailError)
        assertNotNull(state.passwordError)
        assertNotNull(state.confirmPasswordError)
        assertNotNull(state.termsError)
        assertNotNull(state.generalError)
        assertNull(state.registeredUser)
    }

    @Test
    fun testSubmitWithoutTermsAcceptedFails() {
        val store = createStore()
        store.dispatch(RegisterIntent.NameChanged("Budi Santoso"))
        store.dispatch(RegisterIntent.EmailChanged("baru@kmptemplate.org"))
        store.dispatch(RegisterIntent.PasswordChanged("Secret123"))
        store.dispatch(RegisterIntent.ConfirmPasswordChanged("Secret123"))
        assertFalse(store.state.canSubmit)

        store.dispatch(RegisterIntent.SubmitRegister)

        val state = store.state
        assertFalse(state.isSuccess)
        assertNull(state.registeredUser)
        assertEquals("Anda harus menyetujui Syarat & Ketentuan", state.termsError)
        assertNotNull(state.generalError)
    }

    @Test
    fun testRegisterSuccessAutoLogin() {
        val store = createStore()
        store.fillValidForm()
        assertTrue(store.state.canSubmit)

        store.dispatch(RegisterIntent.SubmitRegister)

        val state = store.state
        assertFalse(state.isLoading)
        assertTrue(state.isSuccess)
        assertNull(state.generalError)
        val user = assertNotNull(state.registeredUser)
        assertEquals("Budi Santoso", user.name)
        assertEquals("baru@kmptemplate.org", user.email)
        assertEquals("usr_reg_test", user.id)
        assertEquals("mock_token_register_test", user.token)
    }

    @Test
    fun testRegisterFailureDuplicateEmail() {
        val store = createStore()
        store.fillValidForm(email = TAKEN_EMAIL)
        assertTrue(store.state.canSubmit)

        store.dispatch(RegisterIntent.SubmitRegister)

        val state = store.state
        assertFalse(state.isLoading)
        assertFalse(state.isSuccess)
        assertNull(state.registeredUser)
        assertEquals("Email sudah terdaftar. Silakan gunakan email lain.", state.generalError)
    }

    @Test
    fun testClearErrorsAndReset() {
        val store = createStore()
        store.dispatch(RegisterIntent.SubmitRegister)
        assertNotNull(store.state.generalError)

        store.dispatch(RegisterIntent.ClearErrors)
        assertNull(store.state.nameError)
        assertNull(store.state.emailError)
        assertNull(store.state.passwordError)
        assertNull(store.state.confirmPasswordError)
        assertNull(store.state.termsError)
        assertNull(store.state.generalError)

        store.fillValidForm()
        store.dispatch(RegisterIntent.Reset)
        assertEquals("", store.state.name)
        assertEquals("", store.state.email)
        assertEquals("", store.state.password)
        assertEquals("", store.state.confirmPassword)
        assertFalse(store.state.isTermsAccepted)
        assertFalse(store.state.isSuccess)
    }

    @Test
    fun testSubscriptionUpdates() {
        val store = createStore()
        val states = mutableListOf<RegisterState>()
        val unsubscribe = store.subscribe { states.add(it) }

        store.dispatch(RegisterIntent.NameChanged("Budi Santoso"))
        store.dispatch(RegisterIntent.EmailChanged("baru@kmptemplate.org"))
        unsubscribe()
        store.dispatch(RegisterIntent.Reset)

        // initial state (1) + name changed (2) + email changed (3) = 3 update sebelum unsubscribe
        assertEquals(3, states.size)
        assertEquals("baru@kmptemplate.org", states.last().email)
        assertEquals("Budi Santoso", states.last().name)
    }

    private companion object {
        const val TAKEN_EMAIL = "sudah.ada@kmptemplate.org"
    }
}
