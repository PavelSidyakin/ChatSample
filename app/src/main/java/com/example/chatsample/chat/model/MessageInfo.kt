package com.example.chatsample.chat.model

sealed class MessageInfo(
    open val messageId: Long,
    open val messageText: String,
    open val messageSenderId: Int,
    open val messageSenderName: String,
) {
    data class OutgoingMessage(
        override val messageId: Long,
        override val messageText: String,
        override val messageSenderId: Int,
        override val messageSenderName: String,
        val messageStatus: MessageStatus,
    ) : MessageInfo(messageId, messageText, messageSenderId, messageSenderName) {
        companion object { const val MESSAGE_TYPE_ID = 1 }
    }

    data class IncomingMessage(
        override val messageId: Long,
        override val messageText: String,
        override val messageSenderId: Int,
        override val messageSenderName: String,
    ) : MessageInfo(messageId, messageText, messageSenderId, messageSenderName) {
        companion object { const val MESSAGE_TYPE_ID = 2 }
    }
}