package com.example.chatsample.chat.view

import com.arkivanov.mvikotlin.core.view.MviView
import com.example.chatsample.chat.store.ChatStore

interface ChatView : MviView<ChatStore.State, ChatStore.Intent>