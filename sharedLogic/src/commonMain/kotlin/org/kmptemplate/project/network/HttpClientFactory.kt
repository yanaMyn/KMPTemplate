package org.kmptemplate.project.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private val defaultJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
    encodeDefaults = true
}

internal object ApiConfig {
    const val BASE_URL: String = "https://jsonplaceholder.typicode.com"
}

fun createHttpClient(): HttpClient = HttpClient {
    install(ContentNegotiation) { json(defaultJson) }
    install(Logging) { level = LogLevel.INFO }
    defaultRequest {
        header(HttpHeaders.Accept, ContentType.Application.Json.toString())
    }
}
