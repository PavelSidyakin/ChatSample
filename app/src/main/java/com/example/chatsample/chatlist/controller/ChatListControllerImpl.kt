package com.example.chatsample.chatlist.controller

import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.core.instancekeeper.InstanceKeeperProvider
import com.arkivanov.mvikotlin.core.instancekeeper.get
import com.arkivanov.mvikotlin.core.instancekeeper.getOrCreateStore
import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.chatsample.chatlist.store.ChatListStore
import com.example.chatsample.chatlist.store.ChatListStoreFactory
import com.example.chatsample.chatlist.view.ChatListView
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers

class ChatListControllerImpl @AssistedInject constructor(
    chatListStoreFactory: ChatListStoreFactory,
    @Assisted private val instanceKeeperProvider: InstanceKeeperProvider
): ChatListController {

    private val chatListStore = instanceKeeperProvider.get<ChatListStore>().getOrCreateStore {
        chatListStoreFactory.create()
    }

    override fun onViewCreated(chatListView: ChatListView, viewLifecycle: Lifecycle) {
        bind(
            viewLifecycle, BinderLifecycleMode.CREATE_DESTROY,
            Dispatchers.Main
        ) {
            chatListView.events bindTo chatListStore
        }

        bind(
            viewLifecycle, BinderLifecycleMode.START_STOP,
            Dispatchers.Main
        ) {
            chatListStore.states bindTo chatListView
        }

    }

    @AssistedInject.Factory
    interface Factory {
        fun create(
            lifecycle: Lifecycle,
            instanceKeeperProvider: InstanceKeeperProvider
        ): ChatListControllerImpl
    }

}