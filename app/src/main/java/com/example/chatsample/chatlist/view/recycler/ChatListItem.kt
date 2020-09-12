package com.example.chatsample.chatlist.view.recycler

sealed class ChatListItem {

    sealed class Chat(val chatId: Long): ChatListItem() {
        data class Direct(val id: Long, val name: String): Chat(id)
        data class Group(val id: Long, val name: String): Chat(id)
    }

    class Loading : ChatListItem()
    class Error : ChatListItem()

}
