package com.example.chatsample.chat.controller

import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.core.instancekeeper.InstanceKeeperProvider
import com.arkivanov.mvikotlin.core.instancekeeper.get
import com.arkivanov.mvikotlin.core.instancekeeper.getOrCreateStore
import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.chatsample.chat.store.ChatStore
import com.example.chatsample.chat.store.ChatStoreFactory
import com.example.chatsample.chat.view.ChatView
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers

class ChatControllerImpl @AssistedInject constructor(
    chatStoreFactory: ChatStoreFactory,
    @Assisted private val instanceKeeperProvider: InstanceKeeperProvider
): ChatController {

    private val chatStore = instanceKeeperProvider.get<ChatStore>().getOrCreateStore {
        chatStoreFactory.create()
    }

    override fun onViewCreated(chatView: ChatView, viewLifecycle: Lifecycle) {
        bind(
            viewLifecycle, BinderLifecycleMode.CREATE_DESTROY,
            Dispatchers.Main
        ) {
            chatView.events bindTo chatStore
        }

        bind(
            viewLifecycle, BinderLifecycleMode.START_STOP,
            Dispatchers.Main
        ) {
            chatStore.states bindTo chatView
        }

    }

    @AssistedInject.Factory
    interface Factory {
        fun create(
            instanceKeeperProvider: InstanceKeeperProvider
        ): ChatControllerImpl
    }

}