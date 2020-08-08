package com.example.chatsample.auth.controller

import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.chatsample.auth.store.AuthStore
import com.example.chatsample.auth.store.AuthStoreFactory
import com.example.chatsample.auth.view.AuthEvent
import com.example.chatsample.auth.view.AuthView
import com.example.chatsample.auth.view.AuthViewModel
import com.example.chatsample.domain.ChatRepository
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthControllerImpl @AssistedInject constructor(
    private val storeFactory: StoreFactory,
    private val chatRepository: ChatRepository,
    @Assisted private val lifecycle: Lifecycle
) : AuthController {

    private val authStore = AuthStoreFactory(
        storeFactory = storeFactory,
        chatRepository = chatRepository
    ).create()

    init {
//        bind(lifecycle, BinderLifecycleMode.CREATE_DESTROY, Dispatchers.Main) {
//            //authStore.labels.map { it.toBusEvent() } bindTo { eventBus.send(it)}
//        }
        lifecycle.doOnDestroy(authStore::dispose)
    }


    override fun onViewCreated(authView: AuthView, viewLifecycle: Lifecycle) {
        bind(viewLifecycle, BinderLifecycleMode.CREATE_DESTROY,
            Dispatchers.Main
        ) {
            authView.events bindTo authStore
        }

        bind(viewLifecycle, BinderLifecycleMode.START_STOP,
            Dispatchers.Main
        ) {
            authStore.states bindTo authView
        }

    }

    @AssistedInject.Factory
    interface Factory {
        fun create(lifecycle: Lifecycle): AuthControllerImpl
    }
}