package org.kmptemplate.project.auth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.kmptemplate.project.auth.data.AuthDataSource
import org.kmptemplate.project.auth.data.AuthRepositoryImpl
import org.kmptemplate.project.auth.model.User
import org.kmptemplate.project.auth.mvi.LoginIntent
import org.kmptemplate.project.auth.mvi.LoginStore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginStoreTest {

    private class FakeAuthDataSource : AuthDataSource {
        override suspend fun authenticate(email: String, password: String): Result<User> {
            if (email == "valid@kmptemplate.org" && password == "Secret123") {
                return Result.success(
                    User(
                        id = "usr_test",
                        name = "Test User",
                        email = email,
                        token = "mock_token_test"
                    )
                )
            }
            return Result.failure(IllegalArgumentException("Kredensial tidak valid"))
        }

        override suspend fun register(name: String, email: String, password: String): Result<User> {
            return Result.failure(
                IllegalStateException("Registrasi tidak digunakan pada LoginStoreTest")
            )
        }
    }

    private fun createStore(): LoginStore {
        val repo = AuthRepositoryImpl(FakeAuthDataSource())
        val scope = CoroutineScope(SupervisorJob() + UnconfinedTestDispatcher())
        return LoginStore(repository = repo, scope = scope)
    }

    @Test
    fun testInitialState() {
        val store = createStore()
        val state = store.state.value

        assertEquals("", state.email)
        assertEquals("", state.password)
        assertFalse(state.isPasswordVisible)
        assertFalse(state.isLoading)
        assertFalse(state.isSuccess)
        assertNull(state.emailError)
        assertNull(state.passwordError)
        assertNull(state.generalError)
        assertNull(state.loggedInUser)
        assertFalse(state.canSubmit)
    }

    @Test
    fun testEmailValidation() {
        val store = createStore()

        store.dispatch(LoginIntent.EmailChanged("invalid-email"))
        assertEquals("invalid-email", store.state.value.email)
        assertNotNull(store.state.value.emailError)
        assertFalse(store.state.value.canSubmit)

        store.dispatch(LoginIntent.EmailChanged("valid@kmptemplate.org"))
        assertEquals("valid@kmptemplate.org", store.state.value.email)
        assertNull(store.state.value.emailError)
    }

    @Test
    fun testPasswordValidation() {
        val store = createStore()

        store.dispatch(LoginIntent.PasswordChanged("123"))
        assertEquals("123", store.state.value.password)
        assertNotNull(store.state.value.passwordError)
        assertFalse(store.state.value.canSubmit)

        store.dispatch(LoginIntent.PasswordChanged("Secret123"))
        assertEquals("Secret123", store.state.value.password)
        assertNull(store.state.value.passwordError)
    }

    @Test
    fun testTogglePasswordVisibility() {
        val store = createStore()
        assertFalse(store.state.value.isPasswordVisible)

        store.dispatch(LoginIntent.TogglePasswordVisibility)
        assertTrue(store.state.value.isPasswordVisible)

        store.dispatch(LoginIntent.TogglePasswordVisibility)
        assertFalse(store.state.value.isPasswordVisible)
    }

    @Test
    fun testSubmitLoginWithEmptyFieldsShowsErrors() {
        val store = createStore()
        store.dispatch(LoginIntent.SubmitLogin)

        val state = store.state.value
        assertFalse(state.isSuccess)
        assertNotNull(state.emailError)
        assertNotNull(state.passwordError)
        assertNotNull(state.generalError)
    }

    @Test
    fun testLoginSuccess() = runTest {
        val store = createStore()
        store.dispatch(LoginIntent.EmailChanged("valid@kmptemplate.org"))
        store.dispatch(LoginIntent.PasswordChanged("Secret123"))
        assertTrue(store.state.value.canSubmit)

        store.dispatch(LoginIntent.SubmitLogin)

        val state = store.state.value
        assertFalse(state.isLoading)
        assertTrue(state.isSuccess)
        assertNull(state.generalError)
        val user = state.loggedInUser
        assertNotNull(user)
        assertEquals("Test User", user.name)
        assertEquals("usr_test", user.id)
    }

    @Test
    fun testLoginFailureWithInvalidCredentials() = runTest {
        val store = createStore()
        store.dispatch(LoginIntent.EmailChanged("wrong@kmptemplate.org"))
        store.dispatch(LoginIntent.PasswordChanged("WrongPass123"))
        assertTrue(store.state.value.canSubmit)

        store.dispatch(LoginIntent.SubmitLogin)

        val state = store.state.value
        assertFalse(state.isLoading)
        assertFalse(state.isSuccess)
        assertNull(state.loggedInUser)
        assertEquals("Kredensial tidak valid", state.generalError)
    }

    @Test
    fun testClearErrorsAndReset() {
        val store = createStore()
        store.dispatch(LoginIntent.SubmitLogin)
        assertNotNull(store.state.value.generalError)

        store.dispatch(LoginIntent.ClearErrors)
        assertNull(store.state.value.emailError)
        assertNull(store.state.value.passwordError)
        assertNull(store.state.value.generalError)

        store.dispatch(LoginIntent.EmailChanged("test@kmptemplate.org"))
        store.dispatch(LoginIntent.Reset)
        assertEquals("", store.state.value.email)
        assertFalse(store.state.value.isSuccess)
    }

    @Test
    fun testStateFlowReflectsUpdates() {
        val store = createStore()
        val flow = store.state

        assertEquals("", flow.value.email)

        store.dispatch(LoginIntent.EmailChanged("test@kmptemplate.org"))
        assertEquals("test@kmptemplate.org", flow.value.email)

        store.dispatch(LoginIntent.PasswordChanged("Secret123"))
        assertEquals("Secret123", flow.value.password)

        store.dispatch(LoginIntent.Reset)
        assertEquals("", flow.value.email)
        assertEquals("", flow.value.password)
    }
}
