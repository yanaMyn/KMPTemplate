package org.kmptemplate.project.walkthrough.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.kmptemplate.project.walkthrough.data.WalkthroughRepository
import org.kmptemplate.project.walkthrough.data.WalkthroughRepositoryImpl
import org.kmptemplate.project.walkthrough.model.WalkthroughItem

data class WalkthroughState(
    val items: List<WalkthroughItem> = emptyList(),
    val currentIndex: Int = 0,
    val isCompleted: Boolean = false,
    val isLoading: Boolean = false,
    val loadError: String? = null
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

fun createWalkthroughStore(): WalkthroughStore = WalkthroughStore()

class WalkthroughStore(
    private val repository: WalkthroughRepository = WalkthroughRepositoryImpl(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) {
    private val _state = MutableStateFlow(WalkthroughState(isLoading = true))
    val state: StateFlow<WalkthroughState> = _state.asStateFlow()

    private var loadJob: Job? = null

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

    fun close() {
        scope.cancel()
    }

    private fun handleLoadItems() {
        loadJob?.cancel()
        loadJob = scope.launch {
            _state.update { it.copy(isLoading = true, loadError = null, isCompleted = false) }
            runCatching { repository.fetchWalkthroughItems() }.fold(
                onSuccess = { items ->
                    _state.value = WalkthroughState(
                        items = items,
                        currentIndex = 0,
                        isLoading = false,
                        isCompleted = false,
                        loadError = null
                    )
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            loadError = error.message ?: "Gagal memuat konten. Silakan coba lagi."
                        )
                    }
                }
            )
        }
    }

    private fun handleNextPage() {
        _state.update {
            if (it.isLastPage) {
                it.copy(isCompleted = true)
            } else {
                it.copy(currentIndex = (it.currentIndex + 1).coerceAtMost(it.items.lastIndex))
            }
        }
    }

    private fun handlePreviousPage() {
        _state.update {
            it.copy(currentIndex = (it.currentIndex - 1).coerceAtLeast(0))
        }
    }

    private fun handleSelectPage(index: Int) {
        _state.update {
            if (index in it.items.indices) it.copy(currentIndex = index) else it
        }
    }

    private fun handleSkip() {
        _state.update { it.copy(isCompleted = true) }
    }

    private fun handleComplete() {
        _state.update { it.copy(isCompleted = true) }
    }
}
