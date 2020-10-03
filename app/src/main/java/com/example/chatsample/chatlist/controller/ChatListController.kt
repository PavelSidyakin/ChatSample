package com.example.chatsample.chatlist.controller

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.example.chatsample.chatlist.view.ChatListView

interface ChatListController {

    fun onViewCreated(chatListView: ChatListView, viewLifecycle: Lifecycle)

    interface Dependencies {
        val chatListOutputCallback: (Output) -> Unit
    }

    sealed class Output {
        data class ChatSelected(val chatId: Long) : Output()
    }
}