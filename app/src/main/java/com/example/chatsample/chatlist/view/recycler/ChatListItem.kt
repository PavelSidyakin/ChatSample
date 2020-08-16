package com.example.chatsample.chatlist.view.recycler

sealed class ChatListItem(val chatId: Long) {

    data class Direct(val id: Long, val name: String): ChatListItem(id)
    data class Group(val id: Long, val name: String): ChatListItem(id)

}