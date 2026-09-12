package org.kmptemplate.project.auth.data

import org.kmptemplate.project.auth.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, email: String, password: String): Result<User>
}

class AuthRepositoryImpl(
    private val dataSource: AuthDataSource = RemoteAuthDataSource()
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<User> {
        return dataSource.authenticate(email, password)
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        return dataSource.register(name, email, password)
    }
}
