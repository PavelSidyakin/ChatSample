package com.example.chatsample.chatlist.store

import androidx.paging.PagingData
import com.arkivanov.mvikotlin.core.store.Store
import com.example.chatsample.chatlist.view.recycler.ChatListItem

interface ChatListStore : Store<ChatListStore.Intent, ChatListStore.State, ChatListStore.Label> {

    sealed class Intent {
        class OnRefresh: Intent()
        class OnRetry: Intent()
        data class OnChatSelected(val chatId: Long): Intent()
    }

    data class State(
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val isRetrying: Boolean = false,
        val pagingData: PagingData<ChatListItem>? = null,
        val error: Throwable? = null
    )

    sealed class Label {
    }
}