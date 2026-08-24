package org.kmptemplate.project.walkthrough.data

import org.kmptemplate.project.walkthrough.model.WalkthroughItem

interface WalkthroughRepository {
    fun fetchWalkthroughItems(): List<WalkthroughItem>
}

class WalkthroughRepositoryImpl(
    private val dataSource: WalkthroughDataSource = DefaultWalkthroughDataSource()
) : WalkthroughRepository {
    override fun fetchWalkthroughItems(): List<WalkthroughItem> {
        return dataSource.getWalkthroughItems()
    }
}
