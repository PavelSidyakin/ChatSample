package com.example.chatsample.chat.store

import androidx.paging.PagingData
import com.example.chatsample.chat.view.recycler.MessageListItem

sealed class ChatStateChanges {
    class LoadingStarted(): ChatStateChanges()
    class LoadingCompleted(): ChatStateChanges()
    data class ListChanged(val message: PagingData<MessageListItem>): ChatStateChanges()
    data class ErrorOccurred(val throwable: Throwable): ChatStateChanges()
    data class RefreshStateChanged(val newRefreshState: Boolean): ChatStateChanges()
    data class RetryingStateChanged(val newRetryingState: Boolean): ChatStateChanges()
}