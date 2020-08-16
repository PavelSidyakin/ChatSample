package com.example.chatsample.auth.store

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import javax.inject.Inject

class AuthStoreFactoryImpl @Inject constructor(
    private val storeFactory: StoreFactory,
    private val authIntentExecutor: AuthIntentExecutor
): AuthStoreFactory {

    override fun create(): AuthStore =
        object : AuthStore, Store<AuthStore.Intent, AuthStore.State, AuthStore.Label>
        by storeFactory.create(
            name = "AuthStore",
            initialState = AuthStore.State(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = ::getExecutor,
            reducer = AuthReducer()
        ) {}

    private fun getExecutor(): Executor<AuthStore.Intent,
            Unit, AuthStore.State, AuthStateChanges, AuthStore.Label> = authIntentExecutor

}