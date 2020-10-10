package com.example.chatsample.chat.model

import java.io.Serializable

sealed class MessageInfo(
    open val chatId: Long,
    open val messageId: Long,
    open val messageText: String,
    open val messageSenderId: Int,
    open val messageSenderName: String,
) {
    data class OutgoingMessage(
        override val chatId: Long,
        override val messageId: Long,
        override val messageText: String,
        override val messageSenderId: Int,
        override val messageSenderName: String,
        val messageStatus: MessageStatus,
    ) : MessageInfo(chatId, messageId, messageText, messageSenderId, messageSenderName) {
        companion object { const val MESSAGE_TYPE_ID = 1 }
    }

    data class IncomingMessage(
        override val chatId: Long,
        override val messageId: Long,
        override val messageText: String,
        override val messageSenderId: Int,
        override val messageSenderName: String,
    ) : MessageInfo(chatId, messageId, messageText, messageSenderId, messageSenderName) {
        companion object { const val MESSAGE_TYPE_ID = 2 }
    }
}

data class NextMessageListInfo(
    val fromMessage: Long,
): Serializable

sealed class RequestMessageListResult {
    data class Ok(
        val messages: List<MessageInfo>,
        val nextMessageListInfo: NextMessageListInfo?
    ) : RequestMessageListResult()
}

class UpdateMessageListEvent

