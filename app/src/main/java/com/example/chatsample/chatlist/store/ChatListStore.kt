package com.example.chatsample.chatlist.store

import androidx.paging.PagedList
import com.arkivanov.mvikotlin.core.store.Store
import com.example.chatsample.chatlist.view.recycler.ChatListItem

interface ChatListStore : Store<ChatListStore.Intent, ChatListStore.State, ChatListStore.Label> {

    sealed class Intent {
    }

    data class State(
        val isLoading: Boolean = false,
        val pagedList: PagedList<ChatListItem>? = null,
        val error: Throwable? = null
    )

    sealed class Label {
        data class Dispatched(val id: String) : Label()
    }
}