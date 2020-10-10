package com.example.chatsample.chat.store

import androidx.paging.PagingData
import com.arkivanov.mvikotlin.core.store.Store
import com.example.chatsample.chat.view.recycler.MessageListItem

interface ChatStore : Store<ChatStore.Intent, ChatStore.State, ChatStore.Label> {

    sealed class Intent {
        class Refresh: Intent()
        class Retry: Intent()
        class SendMessage: Intent()
        data class OutgoingMessageText(val message: String): Intent()
    }

    data class State(
        val currentlyEditingMessage: String = "",
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val isRetrying: Boolean = false,
        val pagingData: PagingData<MessageListItem>? = null,
        val error: Throwable? = null
    )

    sealed class Label {
    }
}