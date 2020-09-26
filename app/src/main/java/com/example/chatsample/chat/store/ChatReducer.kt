package com.example.chatsample.chat.store

import com.arkivanov.mvikotlin.core.store.Reducer

class ChatReducer : Reducer<ChatStore.State, ChatStateChanges> {
    override fun ChatStore.State.reduce(result: ChatStateChanges): ChatStore.State {
        return when (result) {
            is ChatStateChanges.ListChanged -> copy(pagingData = result.message)
            is ChatStateChanges.ErrorOccurred -> copy(error = result.throwable)
            is ChatStateChanges.LoadingStarted -> copy(isLoading = true)
            is ChatStateChanges.LoadingCompleted -> copy(isLoading = false)
            is ChatStateChanges.RefreshStateChanged -> copy(isRefreshing = result.newRefreshState)
            is ChatStateChanges.RetryingStateChanged -> copy(isRetrying = result.newRetryingState)
        }
    }
}