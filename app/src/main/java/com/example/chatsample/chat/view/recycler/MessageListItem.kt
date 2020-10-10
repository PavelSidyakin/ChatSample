package com.example.chatsample.chat.view.recycler

import com.example.chatsample.chat.model.MessageStatus

sealed class MessageListItem {

    sealed class Message(
        open val chatId: Long,
        open val messageId: Long
    ): MessageListItem() {
        data class OutgoingMessage(
            override val chatId: Long,
            override val messageId: Long,
            val text: String,
            val status: MessageStatus,
            val temporaryId: Long,
        ): Message(chatId, messageId)

        data class IncomingMessage(
            override val chatId: Long,
            override val messageId: Long,
            val text: String,
            val from: String,
        ): Message(chatId, messageId)
    }

}
