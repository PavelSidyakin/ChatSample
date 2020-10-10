package com.example.chatsample.chat.model

import java.io.Serializable

sealed class MessageInfo(
    open val chatId: Long,
    open val messageId: Long,
    open val messageText: String,
) {
    data class OutgoingMessage(
        override val chatId: Long,
        override val messageId: Long,
        override val messageText: String,
        val messageStatus: MessageStatus,
        val messageTemporaryId: Long,
    ) : MessageInfo(chatId, messageId, messageText) {
        companion object { const val MESSAGE_TYPE_ID = 1 }
    }

    data class IncomingMessage(
        override val chatId: Long,
        override val messageId: Long,
        override val messageText: String,
        val messageSenderId: Int,
        val messageSenderName: String,
    ) : MessageInfo(chatId, messageId, messageText) {
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

