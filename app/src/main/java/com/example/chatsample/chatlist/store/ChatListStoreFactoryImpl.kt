package com.example.chatsample.chatlist.store

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import javax.inject.Inject

class ChatListStoreFactoryImpl @Inject constructor(
    private val storeFactory: StoreFactory,
    private val chatListIntentExecutor: ChatListIntentExecutor
) : ChatListStoreFactory {

    override fun create(): ChatListStore =
        object : ChatListStore, Store<ChatListStore.Intent, ChatListStore.State, ChatListStore.Label>
        by storeFactory.create(
            name = "ChatListStore",
            initialState = ChatListStore.State(),
            bootstrapper = SimpleBootstrapper(ChatListBootstrapper.Action.LoadList()),
            executorFactory = ::getExecutor,
            reducer = ChatListReducer()
        ) {}

    private fun getExecutor(): Executor<ChatListStore.Intent, ChatListBootstrapper.Action, ChatListStore.State, ChatListStateChanges, ChatListStore.Label>
            = chatListIntentExecutor
}