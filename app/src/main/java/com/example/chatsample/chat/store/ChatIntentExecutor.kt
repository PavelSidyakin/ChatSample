package com.example.chatsample.chat.store

import com.arkivanov.mvikotlin.core.store.Executor

interface ChatIntentExecutor : Executor<ChatStore.Intent, ChatBootstrapper.Action, ChatStore.State, ChatStateChanges, ChatStore.Label>