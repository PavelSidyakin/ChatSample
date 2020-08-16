package com.example.chatsample.model

sealed class RequestChatListResult {
    data class Ok(
        val chatMap: Map<Long, ChatInfo>, // chat ID to ChatInfo
        val nextChatListInfo: NextChatListInfo
    ) : RequestChatListResult()
}

data class ChatInfo(
    val chatName: String,
    val chatType: ChatType
)

data class NextChatListInfo(
    val order: Long,
    val chatId: Long
)

data class UpdateChatListEvent(
    val type: UpdateChatListEventType,
    val chatId: Long,
    val chatInfo: ChatInfo?
)

enum class UpdateChatListEventType {
    ADDED,
    REMOVED,
    UPDATED,
}

enum class ChatType {
    DIRECT,
    GROUP,
}