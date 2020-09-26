package com.example.chatsample.chat.controller

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.example.chatsample.chat.view.ChatView

interface ChatController {

    fun onViewCreated(chatView: ChatView, viewLifecycle: Lifecycle)

}