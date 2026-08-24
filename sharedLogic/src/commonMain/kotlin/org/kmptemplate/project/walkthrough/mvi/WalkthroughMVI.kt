package org.kmptemplate.project.walkthrough.mvi

import org.kmptemplate.project.walkthrough.data.WalkthroughRepository
import org.kmptemplate.project.walkthrough.data.WalkthroughRepositoryImpl
import org.kmptemplate.project.walkthrough.model.WalkthroughItem

data class WalkthroughState(
    val items: List<WalkthroughItem> = emptyList(),
    val currentIndex: Int = 0,
    val isCompleted: Boolean = false,
    val isLoading: Boolean = false
) {
    val currentItem: WalkthroughItem?
        get() = items.getOrNull(currentIndex)

    val isLastPage: Boolean
        get() = items.isNotEmpty() && currentIndex == items.lastIndex

    val isFirstPage: Boolean
        get() = currentIndex == 0

    val totalPages: Int
        get() = items.size
}

sealed class WalkthroughIntent {
    data object LoadItems : WalkthroughIntent()
    data object NextPage : WalkthroughIntent()
    data object PreviousPage : WalkthroughIntent()
    data class SelectPage(val index: Int) : WalkthroughIntent()
    data object Skip : WalkthroughIntent()
    data object Complete : WalkthroughIntent()
}

class WalkthroughStore(
    private val repository: WalkthroughRepository = WalkthroughRepositoryImpl()
) {
    private var _state: WalkthroughState = WalkthroughState(isLoading = true)
    val state: WalkthroughState
        get() = _state

    private val listeners = mutableListOf<(WalkthroughState) -> Unit>()

    init {
        dispatch(WalkthroughIntent.LoadItems)
    }

    fun dispatch(intent: WalkthroughIntent) {
        when (intent) {
            is WalkthroughIntent.LoadItems -> handleLoadItems()
            is WalkthroughIntent.NextPage -> handleNextPage()
            is WalkthroughIntent.PreviousPage -> handlePreviousPage()
            is WalkthroughIntent.SelectPage -> handleSelectPage(intent.index)
            is WalkthroughIntent.Skip -> handleSkip()
            is WalkthroughIntent.Complete -> handleComplete()
        }
    }

    private fun updateState(newState: WalkthroughState) {
        _state = newState
        notifyListeners()
    }

    private fun notifyListeners() {
        val currentState = _state
        listeners.forEach { it(currentState) }
    }

    private fun handleLoadItems() {
        val items = repository.fetchWalkthroughItems()
        updateState(
            WalkthroughState(
                items = items,
                currentIndex = 0,
                isLoading = false,
                isCompleted = false
            )
        )
    }

    private fun handleNextPage() {
        val current = _state
        if (current.isLastPage) {
            updateState(current.copy(isCompleted = true))
        } else {
            val nextIndex = (current.currentIndex + 1).coerceAtMost(current.items.lastIndex)
            updateState(current.copy(currentIndex = nextIndex))
        }
    }

    private fun handlePreviousPage() {
        val current = _state
        val prevIndex = (current.currentIndex - 1).coerceAtLeast(0)
        updateState(current.copy(currentIndex = prevIndex))
    }

    private fun handleSelectPage(index: Int) {
        val current = _state
        if (index in current.items.indices) {
            updateState(current.copy(currentIndex = index))
        }
    }

    private fun handleSkip() {
        updateState(_state.copy(isCompleted = true))
    }

    private fun handleComplete() {
        updateState(_state.copy(isCompleted = true))
    }

    /**
     * Subscribe to state updates. Returns an unsubscribe function.
     * Invokes listener immediately with the current state.
     */
    fun subscribe(listener: (WalkthroughState) -> Unit): () -> Unit {
        listeners.add(listener)
        listener(_state)
        return {
            listeners.remove(listener)
        }
    }
}
