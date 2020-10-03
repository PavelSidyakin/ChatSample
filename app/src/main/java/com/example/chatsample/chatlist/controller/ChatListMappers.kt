package com.example.chatsample.chatlist.controller

import com.example.chatsample.chatlist.store.ChatListStore

val chatListIntentToOutput: ChatListStore.Intent.() -> ChatListController.Output? = {
        when (this) {
            is ChatListStore.Intent.OnChatSelected -> ChatListController.Output.ChatSelected(chatId)
            else -> null
        }
    }
