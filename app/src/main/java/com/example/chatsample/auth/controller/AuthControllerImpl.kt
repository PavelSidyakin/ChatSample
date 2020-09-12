package com.example.chatsample.auth.controller

import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.core.instancekeeper.InstanceKeeperProvider
import com.arkivanov.mvikotlin.core.instancekeeper.get
import com.arkivanov.mvikotlin.core.instancekeeper.getOrCreateStore
import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.chatsample.auth.store.AuthStore
import com.example.chatsample.auth.store.AuthStoreFactory
import com.example.chatsample.auth.view.AuthView
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers

class AuthControllerImpl @AssistedInject constructor(
    authStoreFactory: AuthStoreFactory,
    @Assisted private val lifecycle: Lifecycle,
    @Assisted private val instanceKeeperProvider: InstanceKeeperProvider
) : AuthController {

    private val authStore = instanceKeeperProvider.get<AuthStore>().getOrCreateStore {
        authStoreFactory.create()
    }

    init {
        lifecycle.doOnDestroy(authStore::dispose)
    }

    override fun onViewCreated(authView: AuthView, viewLifecycle: Lifecycle) {
        bind(
            viewLifecycle, BinderLifecycleMode.CREATE_DESTROY,
            Dispatchers.Main
        ) {
            authView.events bindTo authStore
        }

        bind(
            viewLifecycle, BinderLifecycleMode.START_STOP,
            Dispatchers.Main
        ) {
            authStore.states bindTo authView
        }

    }

    @AssistedInject.Factory
    interface Factory {
        fun create(
            lifecycle: Lifecycle,
            instanceKeeperProvider: InstanceKeeperProvider
        ): AuthControllerImpl
    }
}