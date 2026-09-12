package org.kmptemplate.project.walkthrough.data.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class PostDto(
    val id: Int,
    val title: String,
    val body: String
)
