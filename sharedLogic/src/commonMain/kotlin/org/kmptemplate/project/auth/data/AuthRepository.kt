package org.kmptemplate.project.auth.data

import org.kmptemplate.project.auth.model.User

interface AuthRepository {
    fun login(email: String, password: String): Result<User>
    fun register(name: String, email: String, password: String): Result<User>
}

class AuthRepositoryImpl(
    private val dataSource: AuthDataSource = DefaultAuthDataSource()
) : AuthRepository {
    override fun login(email: String, password: String): Result<User> {
        return dataSource.authenticate(email, password)
    }

    override fun register(name: String, email: String, password: String): Result<User> {
        return dataSource.register(name, email, password)
    }
}
