package org.kmptemplate.project.auth

import androidx.lifecycle.ViewModel
import org.kmptemplate.project.auth.mvi.LoginStore

/**
 * Bungkus [LoginStore] agar terikat pada [androidx.lifecycle.ViewModelStore] milik host
 * (Activity / NavBackStackEntry). Store survives config change; [onCleared] menutup
 * `CoroutineScope` internal store ketika layar benar-benar hancur.
 */
class LoginViewModel(val store: LoginStore) : ViewModel() {
    override fun onCleared() {
        store.close()
    }
}
