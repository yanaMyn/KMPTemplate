package org.kmptemplate.project.di

import org.koin.mp.KoinPlatform
import org.kmptemplate.project.auth.mvi.LoginStore
import org.kmptemplate.project.auth.mvi.RegisterStore
import org.kmptemplate.project.walkthrough.mvi.WalkthroughStore

/**
 * Helper resolver Koin untuk konsumen native (Swift).
 *
 * Kotlin/Native tidak dapat memanggil `Koin.get<T>()` reified generic dari Swift,
 * jadi kita ekspos fungsi non-generic per tipe.
 */
fun getLoginStore(): LoginStore = KoinPlatform.getKoin().get()

fun getRegisterStore(): RegisterStore = KoinPlatform.getKoin().get()

fun getWalkthroughStore(): WalkthroughStore = KoinPlatform.getKoin().get()
