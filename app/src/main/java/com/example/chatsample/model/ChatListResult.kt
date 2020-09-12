package com.example.chatsample.model

import java.io.Serializable

sealed class RequestChatListResult {
    data class Ok(
        val chats: List<ChatInfo>,
        val nextChatListInfo: NextChatListInfo?
    ) : RequestChatListResult()
}

data class ChatInfo(
    val chatId: Long,
    val chatName: String,
    val chatType: ChatType,
    val chatOrder: Long
)

data class NextChatListInfo(
    val order: Long,
    val chatId: Long
) : Serializable

data class UpdateChatListEvent(
    val chatInfo: ChatInfo
)

enum class ChatType(val id: Int) {
    DIRECT(1),
    GROUP(2),
    ;

    companion object {
        fun byId(id: Int): ChatType {
            return values().find { it.id == id } ?: throw RuntimeException("Wrong type id: $id")
        }
    }

}
