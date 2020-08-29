package com.example.chatsample.chatlist.store

import androidx.paging.PagingData
import com.example.chatsample.chatlist.view.recycler.ChatListItem

sealed class ChatListStateChanges {
    class LoadingStarted(): ChatListStateChanges()
    class LoadingCompleted(): ChatListStateChanges()
    data class ListChanged(val chatList: PagingData<ChatListItem>): ChatListStateChanges()
    data class ErrorOccurred(val throwable: Throwable): ChatListStateChanges()
}