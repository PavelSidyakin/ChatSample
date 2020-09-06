package com.example.chatsample.chatlist.store

import androidx.paging.PagingData
import com.arkivanov.mvikotlin.core.store.Store
import com.example.chatsample.chatlist.view.recycler.ChatListItem

interface ChatListStore : Store<ChatListStore.Intent, ChatListStore.State, ChatListStore.Label> {

    sealed class Intent {
        class Refresh: Intent()
        data class LoadingState(val isLoading: Boolean): Intent()
    }

    data class State(
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val pagingData: PagingData<ChatListItem>? = null,
        val error: Throwable? = null
    )

    sealed class Label {
        data class Dispatched(val id: String) : Label()
    }
}