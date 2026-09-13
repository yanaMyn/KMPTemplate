package org.kmptemplate.project.auth

import androidx.lifecycle.ViewModel
import org.kmptemplate.project.auth.mvi.RegisterStore

class RegisterViewModel(val store: RegisterStore) : ViewModel() {
    override fun onCleared() {
        store.close()
    }
}
