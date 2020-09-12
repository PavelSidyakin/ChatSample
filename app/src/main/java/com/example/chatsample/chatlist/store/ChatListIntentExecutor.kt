package com.example.chatsample.chatlist.store

import com.arkivanov.mvikotlin.core.store.Executor

interface ChatListIntentExecutor : Executor<ChatListStore.Intent, ChatListBootstrapper.Action, ChatListStore.State, ChatListStateChanges, ChatListStore.Label>