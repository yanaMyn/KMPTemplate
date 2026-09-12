package org.kmptemplate.project.walkthrough.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.kmptemplate.project.network.ApiConfig
import org.kmptemplate.project.network.createHttpClient
import org.kmptemplate.project.walkthrough.data.dto.PostDto
import org.kmptemplate.project.walkthrough.model.WalkthroughItem

interface WalkthroughDataSource {
    suspend fun getWalkthroughItems(): List<WalkthroughItem>
}

/**
 * Mengambil 3 post pertama dari https://jsonplaceholder.typicode.com/posts
 * dan memetakannya menjadi WalkthroughItem.
 */
class RemoteWalkthroughDataSource(
    private val client: HttpClient = createHttpClient()
) : WalkthroughDataSource {

    override suspend fun getWalkthroughItems(): List<WalkthroughItem> {
        val posts: List<PostDto> = client.get("${ApiConfig.BASE_URL}/posts") {
            parameter("_limit", MAX_ITEMS)
        }.body()

        return posts.take(MAX_ITEMS).mapIndexed { index, post ->
            WalkthroughItem(
                id = post.id,
                title = post.title.replaceFirstChar { it.uppercase() },
                description = post.body.replace('\n', ' '),
                iconName = ICONS[index % ICONS.size]
            )
        }
    }

    private companion object {
        const val MAX_ITEMS = 3
        val ICONS = listOf("sparkles", "layers", "devices")
    }
}
