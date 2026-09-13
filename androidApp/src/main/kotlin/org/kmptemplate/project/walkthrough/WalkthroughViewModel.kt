package org.kmptemplate.project.walkthrough

import androidx.lifecycle.ViewModel
import org.kmptemplate.project.walkthrough.mvi.WalkthroughStore

class WalkthroughViewModel(val store: WalkthroughStore) : ViewModel() {
    override fun onCleared() {
        store.close()
    }
}
