package com.example.chatsample.chatlist.store

import com.arkivanov.mvikotlin.core.store.Reducer

class ChatListReducer : Reducer<ChatListStore.State, ChatListStateChanges> {
    override fun ChatListStore.State.reduce(result: ChatListStateChanges): ChatListStore.State {
        return when (result) {
            is ChatListStateChanges.ListChanged -> copy(pagingData = result.chatList)
            is ChatListStateChanges.ErrorOccurred -> copy(error = result.throwable)
            is ChatListStateChanges.LoadingStarted -> copy(isLoading = true)
            is ChatListStateChanges.LoadingCompleted -> copy(isLoading = false)
            is ChatListStateChanges.RefreshStateChanged -> copy(isRefreshing = result.newRefreshState)
        }
    }
}