package org.kmptemplate.project.walkthrough

import org.kmptemplate.project.walkthrough.data.WalkthroughDataSource
import org.kmptemplate.project.walkthrough.data.WalkthroughRepositoryImpl
import org.kmptemplate.project.walkthrough.model.WalkthroughItem
import org.kmptemplate.project.walkthrough.mvi.WalkthroughIntent
import org.kmptemplate.project.walkthrough.mvi.WalkthroughState
import org.kmptemplate.project.walkthrough.mvi.WalkthroughStore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WalkthroughStoreTest {

    private class FakeDataSource : WalkthroughDataSource {
        override fun getWalkthroughItems(): List<WalkthroughItem> {
            return listOf(
                WalkthroughItem(1, "Page 1", "Desc 1", "icon1"),
                WalkthroughItem(2, "Page 2", "Desc 2", "icon2"),
                WalkthroughItem(3, "Page 3", "Desc 3", "icon3")
            )
        }
    }

    private fun createStore(): WalkthroughStore {
        val repo = WalkthroughRepositoryImpl(FakeDataSource())
        return WalkthroughStore(repository = repo)
    }

    @Test
    fun testInitialState() {
        val store = createStore()
        val state = store.state

        assertFalse(state.isLoading)
        assertEquals(3, state.items.size)
        assertEquals(0, state.currentIndex)
        assertEquals("Page 1", state.currentItem?.title)
        assertTrue(state.isFirstPage)
        assertFalse(state.isLastPage)
        assertFalse(state.isCompleted)
    }

    @Test
    fun testNextPage() {
        val store = createStore()
        store.dispatch(WalkthroughIntent.NextPage)

        val state = store.state
        assertEquals(1, state.currentIndex)
        assertEquals("Page 2", state.currentItem?.title)
        assertFalse(state.isFirstPage)
        assertFalse(state.isLastPage)
    }

    @Test
    fun testPreviousPage() {
        val store = createStore()
        store.dispatch(WalkthroughIntent.NextPage)
        assertEquals(1, store.state.currentIndex)

        store.dispatch(WalkthroughIntent.PreviousPage)
        assertEquals(0, store.state.currentIndex)

        store.dispatch(WalkthroughIntent.PreviousPage)
        assertEquals(0, store.state.currentIndex)
    }

    @Test
    fun testNextPageOnLastPageCompletes() {
        val store = createStore()
        store.dispatch(WalkthroughIntent.SelectPage(2))
        assertTrue(store.state.isLastPage)

        store.dispatch(WalkthroughIntent.NextPage)
        assertTrue(store.state.isCompleted)
    }

    @Test
    fun testSkipIntent() {
        val store = createStore()
        store.dispatch(WalkthroughIntent.Skip)
        assertTrue(store.state.isCompleted)
    }

    @Test
    fun testCompleteIntent() {
        val store = createStore()
        store.dispatch(WalkthroughIntent.Complete)
        assertTrue(store.state.isCompleted)
    }

    @Test
    fun testSubscriptionUpdates() {
        val store = createStore()
        val states = mutableListOf<WalkthroughState>()
        val unsubscribe = store.subscribe { states.add(it) }

        store.dispatch(WalkthroughIntent.NextPage)
        store.dispatch(WalkthroughIntent.NextPage)
        unsubscribe()
        store.dispatch(WalkthroughIntent.PreviousPage)

        // initial state + 2 next page updates = 3
        assertEquals(3, states.size)
        assertEquals(2, states.last().currentIndex)
    }
}
