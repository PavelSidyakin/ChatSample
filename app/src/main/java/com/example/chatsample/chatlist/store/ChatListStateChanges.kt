package com.example.chatsample.chatlist.store

import androidx.paging.PagedList
import com.example.chatsample.chatlist.view.recycler.ChatListItem

sealed class ChatListStateChanges {
    class LoadingStarted(): ChatListStateChanges()
    class LoadingCompleted(): ChatListStateChanges()
    data class ListChanged(val chatList: PagedList<ChatListItem>): ChatListStateChanges()
    data class ErrorOccurred(val throwable: Throwable): ChatListStateChanges()
}