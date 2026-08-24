package org.kmptemplate.project.auth.data

import org.kmptemplate.project.auth.model.User

interface AuthRepository {
    fun login(email: String, password: String): Result<User>
    fun register(name: String, email: String, password: String): Result<User>
    fun logout()
    fun getCurrentUser(): User?
}

class AuthRepositoryImpl(
    private val dataSource: AuthDataSource = DefaultAuthDataSource()
) : AuthRepository {
    private var currentUser: User? = null

    override fun login(email: String, password: String): Result<User> {
        val result = dataSource.authenticate(email, password)
        result.onSuccess { user ->
            currentUser = user
        }
        return result
    }

    /**
     * Mendaftarkan akun baru. Pendaftaran yang berhasil langsung menandai user
     * sebagai user aktif (auto-login), konsisten dengan perilaku [login].
     */
    override fun register(name: String, email: String, password: String): Result<User> {
        val result = dataSource.register(name, email, password)
        result.onSuccess { user ->
            currentUser = user
        }
        return result
    }

    override fun logout() {
        currentUser = null
    }

    override fun getCurrentUser(): User? {
        return currentUser
    }
}
