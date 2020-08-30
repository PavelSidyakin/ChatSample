package com.example.chatsample.chatlist.view.recycler

import com.example.chatsample.model.ChatType

sealed class ChatListItem(val chatId: Long) {

    data class Direct(val id: Long, val name: String): ChatListItem(id)
    data class Group(val id: Long, val name: String): ChatListItem(id)

}

data class ChatListItem2(
    val id: Long,
    val name: String,
    val type: ChatType
)