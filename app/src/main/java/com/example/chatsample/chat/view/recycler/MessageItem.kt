package com.example.chatsample.chat.view.recycler

sealed class MessageItem {

    data class Message(
        val messageId: Long,
        val text: String,
        val from: String,
    ): MessageItem()

}
