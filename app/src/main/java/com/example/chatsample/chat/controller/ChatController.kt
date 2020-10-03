package com.example.chatsample.chat.controller

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.example.chatsample.chat.view.ChatView
import com.example.chatsample.chatlist.controller.ChatListController

interface ChatController {

    fun onViewCreated(chatView: ChatView, viewLifecycle: Lifecycle)

    interface Dependencies {
        val chatId: Long
    }

}