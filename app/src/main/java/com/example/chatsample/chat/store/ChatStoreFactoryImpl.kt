package com.example.chatsample.chat.store

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import javax.inject.Inject

class ChatStoreFactoryImpl @Inject constructor(
    private val storeFactory: StoreFactory,
    private val chatIntentExecutor: ChatIntentExecutor
) : ChatStoreFactory {

    override fun create(): ChatStore =
        object : ChatStore, Store<ChatStore.Intent, ChatStore.State, ChatStore.Label>
        by storeFactory.create(
            name = "ChatListStore",
            initialState = ChatStore.State(),
            bootstrapper = SimpleBootstrapper(ChatBootstrapper.Action.LoadList()),
            executorFactory = ::getExecutor,
            reducer = ChatReducer()
        ) {}

    private fun getExecutor(): Executor<ChatStore.Intent, ChatBootstrapper.Action, ChatStore.State, ChatStateChanges, ChatStore.Label>
            = chatIntentExecutor
}