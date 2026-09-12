package org.kmptemplate.project.walkthrough.data

import org.kmptemplate.project.walkthrough.model.WalkthroughItem

interface WalkthroughRepository {
    suspend fun fetchWalkthroughItems(): List<WalkthroughItem>
}

class WalkthroughRepositoryImpl(
    private val dataSource: WalkthroughDataSource = RemoteWalkthroughDataSource()
) : WalkthroughRepository {
    override suspend fun fetchWalkthroughItems(): List<WalkthroughItem> {
        return dataSource.getWalkthroughItems()
    }
}
