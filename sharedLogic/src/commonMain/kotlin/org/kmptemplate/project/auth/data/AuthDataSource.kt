package org.kmptemplate.project.auth.data

import org.kmptemplate.project.auth.model.User

interface AuthDataSource {
    fun authenticate(email: String, password: String): Result<User>
}

class DefaultAuthDataSource : AuthDataSource {
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

        return Result.failure(IllegalArgumentException("Email atau kata sandi tidak valid."))
    }
}
