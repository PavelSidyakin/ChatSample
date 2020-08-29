package com.example.chatsample.chatlist.store

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.arkivanov.mvikotlin.extensions.coroutines.SuspendExecutor
import com.example.chatsample.chatlist.store.paging.ChatListDataSource
import com.example.chatsample.chatlist.store.repository.ChatNetworkRepository
import com.example.chatsample.chatlist.view.recycler.ChatListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class ChatListIntentExecutorImpl @Inject constructor(
    private val chatNetworkRepository: ChatNetworkRepository
) : SuspendExecutor<ChatListStore.Intent, ChatListBootstrapper.Action, ChatListStore.State, ChatListStateChanges, ChatListStore.Label>(
    mainContext = Dispatchers.Main
), ChatListIntentExecutor {

    private var retryChannel = Channel<Any>(1)

    private val pageListConfig by lazy {
        PagingConfig(pageSize = 20, initialLoadSize = 25, enablePlaceholders = false)
    }

    override suspend fun executeAction(action: ChatListBootstrapper.Action, getState: () -> ChatListStore.State) {
        when (action) {
            is ChatListBootstrapper.Action.LoadList -> handleActionLoadList()
        }
    }

    private suspend fun handleActionLoadList() = coroutineScope {
        buildDataSource(this).collectLatest { pagingData ->
            dispatch(ChatListStateChanges.ListChanged(pagingData))
        }
    }

    private fun buildDataSource(coroutineScope: CoroutineScope): Flow<PagingData<ChatListItem>> {
        return Pager(config = pageListConfig, pagingSourceFactory = { ChatListDataSource({ dispatch(it) }, chatNetworkRepository, coroutineScope.coroutineContext, retryChannel) })
            .flow
            .cachedIn(coroutineScope)
    }

}