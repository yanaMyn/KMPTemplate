package org.kmptemplate.project.auth.data

import kotlinx.coroutines.delay
import org.kmptemplate.project.auth.model.User

/**
 * Dev-only auth data source. Dipakai ketika `KMPT_MOCK_AUTH=true`.
 *
 * Aturan:
 *  - `authenticate`: apapun password-nya, kalau bukan [DEMO_PASSWORD] → failure.
 *    Kalau benar → success untuk email apapun.
 *  - `register`: selalu success — nama & email di-echo balik.
 *  - Latency palsu ~350ms supaya spinner tetap muncul di UI.
 */
class MockAuthDataSource : AuthDataSource {

    override suspend fun authenticate(email: String, password: String): Result<User> {
        delay(MOCK_LATENCY_MS)
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (trimmedPassword != DEMO_PASSWORD) {
            return Result.failure(
                IllegalArgumentException(
                    "Email atau kata sandi tidak valid (mock). Gunakan kata sandi \"$DEMO_PASSWORD\"."
                )
            )
        }

        val displayName = trimmedEmail
            .substringBefore('@')
            .ifBlank { "Mock User" }
            .replaceFirstChar { it.uppercase() }

        return Result.success(
            User(
                id = "usr_mock_${trimmedEmail.lowercase()}",
                name = "$displayName (Mock)",
                email = trimmedEmail,
                token = "mock_token_login_${trimmedEmail.lowercase()}"
            )
        )
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        delay(MOCK_LATENCY_MS)
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()
        return Result.success(
            User(
                id = "usr_mock_new_${trimmedEmail.lowercase()}",
                name = trimmedName.ifBlank { "Mock User" },
                email = trimmedEmail,
                token = "mock_token_register_${trimmedEmail.lowercase()}"
            )
        )
    }

    private companion object {
        const val DEMO_PASSWORD = "Password123!"
        const val MOCK_LATENCY_MS = 350L
    }
}
