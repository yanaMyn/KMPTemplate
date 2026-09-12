package org.kmptemplate.project.auth.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import org.kmptemplate.project.auth.data.dto.UserDto
import org.kmptemplate.project.auth.model.User
import org.kmptemplate.project.network.ApiConfig
import org.kmptemplate.project.network.createHttpClient

interface AuthDataSource {
    suspend fun authenticate(email: String, password: String): Result<User>
    suspend fun register(name: String, email: String, password: String): Result<User>
}

/**
 * Data source untuk auth yang meng-hit https://jsonplaceholder.typicode.com.
 *
 * Placeholder API tidak menyediakan endpoint autentikasi, jadi:
 *  - `authenticate` = `GET /users?email=<email>`; kata sandi dicek client-side terhadap
 *    kata sandi demo tetap `Password123!`.
 *  - `register` = `POST /users` (server placeholder membalas id 11).
 */
class RemoteAuthDataSource(
    private val client: HttpClient = createHttpClient()
) : AuthDataSource {

    override suspend fun authenticate(email: String, password: String): Result<User> {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (trimmedPassword != DEMO_PASSWORD) {
            return Result.failure(
                IllegalArgumentException(
                    "Email atau kata sandi tidak valid. Untuk demo, gunakan kata sandi \"$DEMO_PASSWORD\"."
                )
            )
        }

        return runCatching {
            val response: HttpResponse = client.get("${ApiConfig.BASE_URL}/users") {
                parameter("email", trimmedEmail)
            }
            if (!response.status.isSuccess()) {
                error("Login gagal (${response.status.value}).")
            }
            val users: List<UserDto> = response.body()
            val match = users.firstOrNull { it.email.equals(trimmedEmail, ignoreCase = true) }
                ?: error("Email tidak terdaftar. Coba email dari jsonplaceholder (mis. Sincere@april.biz).")
            match.toUser()
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()

        return runCatching {
            val response: HttpResponse = client.post("${ApiConfig.BASE_URL}/users") {
                contentType(ContentType.Application.Json)
                setBody(
                    RegisterRequest(
                        name = trimmedName,
                        username = trimmedName.substringBefore(' ').lowercase(),
                        email = trimmedEmail
                    )
                )
            }
            if (response.status != HttpStatusCode.Created && !response.status.isSuccess()) {
                error("Registrasi gagal (${response.status.value}).")
            }
            response.body<UserDto>().toUser(fallbackName = trimmedName, fallbackEmail = trimmedEmail)
        }
    }

    @Serializable
    private data class RegisterRequest(
        val name: String,
        val username: String,
        val email: String
    )

    private fun UserDto.toUser(
        fallbackName: String = name,
        fallbackEmail: String = email
    ): User = User(
        id = "usr_$id",
        name = name.ifBlank { fallbackName },
        email = email.ifBlank { fallbackEmail },
        token = "jsonplaceholder_token_$id"
    )

    private companion object {
        const val DEMO_PASSWORD = "Password123!"
    }
}
