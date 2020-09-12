package com.example.chatsample.chatlist.view

import com.arkivanov.mvikotlin.core.view.MviView
import com.example.chatsample.chatlist.store.ChatListStore

interface ChatListView : MviView<ChatListStore.State, ChatListStore.Intent>