package org.kmptemplate.project.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class UserDto(
    val id: Int = 0,
    val name: String = "",
    val username: String = "",
    val email: String = ""
)
