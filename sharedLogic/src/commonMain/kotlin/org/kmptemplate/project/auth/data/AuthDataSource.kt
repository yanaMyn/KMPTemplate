package org.kmptemplate.project.auth.data

import org.kmptemplate.project.auth.model.User

interface AuthDataSource {
    fun authenticate(email: String, password: String): Result<User>
    fun register(name: String, email: String, password: String): Result<User>
}

class DefaultAuthDataSource : AuthDataSource {
    /**
     * In-memory storage untuk akun hasil registrasi (User + password).
     * Hanya untuk keperluan template/demo — bukan penyimpanan persisten.
     */
    private val registeredUsers = mutableListOf<Pair<User, String>>()

    override fun authenticate(email: String, password: String): Result<User> {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (trimmedEmail.equals("admin@kmptemplate.org", ignoreCase = true) && trimmedPassword == "Password123!") {
            return Result.success(
                User(
                    id = "usr_001",
                    name = "Admin User",
                    email = trimmedEmail,
                    token = "mock_jwt_token_admin_kmptemplate"
                )
            )
        }

        if (trimmedEmail.equals("user@kmptemplate.org", ignoreCase = true) && trimmedPassword == "Password123!") {
            return Result.success(
                User(
                    id = "usr_002",
                    name = "Standard User",
                    email = trimmedEmail,
                    token = "mock_jwt_token_user_kmptemplate"
                )
            )
        }

        val registered = registeredUsers.firstOrNull { (user, storedPassword) ->
            user.email.equals(trimmedEmail, ignoreCase = true) && storedPassword == trimmedPassword
        }
        if (registered != null) {
            return Result.success(registered.first)
        }

        return Result.failure(IllegalArgumentException("Email atau kata sandi tidak valid."))
    }

    override fun register(name: String, email: String, password: String): Result<User> {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (isEmailTaken(trimmedEmail)) {
            return Result.failure(
                IllegalArgumentException("Email sudah terdaftar. Silakan gunakan email lain.")
            )
        }

        val userId = "usr_reg_" + (registeredUsers.size + 1)
        val user = User(
            id = userId,
            name = trimmedName,
            email = trimmedEmail,
            token = "mock_jwt_token_" + userId
        )
        registeredUsers.add(user to trimmedPassword)
        return Result.success(user)
    }

    private fun isEmailTaken(email: String): Boolean {
        val demoEmails = listOf("admin@kmptemplate.org", "user@kmptemplate.org")
        if (demoEmails.any { it.equals(email, ignoreCase = true) }) {
            return true
        }
        return registeredUsers.any { (user, _) -> user.email.equals(email, ignoreCase = true) }
    }
}
