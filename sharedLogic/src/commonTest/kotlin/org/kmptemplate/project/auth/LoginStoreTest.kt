package org.kmptemplate.project.auth

import org.kmptemplate.project.auth.data.AuthDataSource
import org.kmptemplate.project.auth.data.AuthRepositoryImpl
import org.kmptemplate.project.auth.model.User
import org.kmptemplate.project.auth.mvi.LoginIntent
import org.kmptemplate.project.auth.mvi.LoginState
import org.kmptemplate.project.auth.mvi.LoginStore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LoginStoreTest {

    private class FakeAuthDataSource : AuthDataSource {
        override fun authenticate(email: String, password: String): Result<User> {
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
    }

    private fun createStore(): LoginStore {
        val repo = AuthRepositoryImpl(FakeAuthDataSource())
        return LoginStore(repository = repo)
    }

    @Test
    fun testInitialState() {
        val store = createStore()
        val state = store.state

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

        // Invalid email format
        store.dispatch(LoginIntent.EmailChanged("invalid-email"))
        assertEquals("invalid-email", store.state.email)
        assertNotNull(store.state.emailError)
        assertFalse(store.state.canSubmit)

        // Valid email format
        store.dispatch(LoginIntent.EmailChanged("valid@kmptemplate.org"))
        assertEquals("valid@kmptemplate.org", store.state.email)
        assertNull(store.state.emailError)
    }

    @Test
    fun testPasswordValidation() {
        val store = createStore()

        // Password too short
        store.dispatch(LoginIntent.PasswordChanged("123"))
        assertEquals("123", store.state.password)
        assertNotNull(store.state.passwordError)
        assertFalse(store.state.canSubmit)

        // Valid password length
        store.dispatch(LoginIntent.PasswordChanged("Secret123"))
        assertEquals("Secret123", store.state.password)
        assertNull(store.state.passwordError)
    }

    @Test
    fun testTogglePasswordVisibility() {
        val store = createStore()
        assertFalse(store.state.isPasswordVisible)

        store.dispatch(LoginIntent.TogglePasswordVisibility)
        assertTrue(store.state.isPasswordVisible)

        store.dispatch(LoginIntent.TogglePasswordVisibility)
        assertFalse(store.state.isPasswordVisible)
    }

    @Test
    fun testSubmitLoginWithEmptyFieldsShowsErrors() {
        val store = createStore()
        store.dispatch(LoginIntent.SubmitLogin)

        val state = store.state
        assertFalse(state.isSuccess)
        assertNotNull(state.emailError)
        assertNotNull(state.passwordError)
        assertNotNull(state.generalError)
    }

    @Test
    fun testLoginSuccess() {
        val store = createStore()
        store.dispatch(LoginIntent.EmailChanged("valid@kmptemplate.org"))
        store.dispatch(LoginIntent.PasswordChanged("Secret123"))
        assertTrue(store.state.canSubmit)

        store.dispatch(LoginIntent.SubmitLogin)

        val state = store.state
        assertFalse(state.isLoading)
        assertTrue(state.isSuccess)
        assertNull(state.generalError)
        val user = state.loggedInUser
        assertNotNull(user)
        assertEquals("Test User", user.name)
        assertEquals("usr_test", user.id)
    }

    @Test
    fun testLoginFailureWithInvalidCredentials() {
        val store = createStore()
        store.dispatch(LoginIntent.EmailChanged("wrong@kmptemplate.org"))
        store.dispatch(LoginIntent.PasswordChanged("WrongPass123"))
        assertTrue(store.state.canSubmit)

        store.dispatch(LoginIntent.SubmitLogin)

        val state = store.state
        assertFalse(state.isLoading)
        assertFalse(state.isSuccess)
        assertNull(state.loggedInUser)
        assertEquals("Kredensial tidak valid", state.generalError)
    }

    @Test
    fun testClearErrorsAndReset() {
        val store = createStore()
        store.dispatch(LoginIntent.SubmitLogin)
        assertNotNull(store.state.generalError)

        store.dispatch(LoginIntent.ClearErrors)
        assertNull(store.state.emailError)
        assertNull(store.state.passwordError)
        assertNull(store.state.generalError)

        store.dispatch(LoginIntent.EmailChanged("test@kmptemplate.org"))
        store.dispatch(LoginIntent.Reset)
        assertEquals("", store.state.email)
        assertFalse(store.state.isSuccess)
    }

    @Test
    fun testSubscriptionUpdates() {
        val store = createStore()
        val states = mutableListOf<LoginState>()
        val unsubscribe = store.subscribe { states.add(it) }

        store.dispatch(LoginIntent.EmailChanged("test@kmptemplate.org"))
        store.dispatch(LoginIntent.PasswordChanged("Secret123"))
        unsubscribe()
        store.dispatch(LoginIntent.Reset)

        // initial state (1) + email changed (2) + password changed (3) = 3 updates received before unsubscribe
        assertEquals(3, states.size)
        assertEquals("Secret123", states.last().password)
    }
}
