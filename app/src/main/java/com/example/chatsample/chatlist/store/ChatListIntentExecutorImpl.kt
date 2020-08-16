package com.example.chatsample.chatlist.store

import androidx.paging.PagedList
import com.arkivanov.mvikotlin.extensions.coroutines.SuspendExecutor
import com.example.chatsample.chatlist.store.paging.ChatListDataSourceFactory
import com.example.chatsample.chatlist.view.recycler.ChatListItem
import com.example.chatsample.data.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatListIntentExecutorImpl @Inject constructor(
    private val chatRepository: ChatRepository
) : SuspendExecutor<ChatListStore.Intent, ChatListBootstrapper.Action, ChatListStore.State, ChatListStateChanges, ChatListStore.Label>(
    mainContext = Dispatchers.Main
), ChatListIntentExecutor {

    private var retryChannel = Channel<Any>(1)

    private val pageListConfig by lazy {
        PagedList.Config.Builder()
            .setPageSize(20)
            .setInitialLoadSizeHint(25)
            .setEnablePlaceholders(false)
            .build()
    }

    override suspend fun executeAction(action: ChatListBootstrapper.Action, getState: () -> ChatListStore.State) {
        when (action) {
            is ChatListBootstrapper.Action.LoadList -> handleActionLoadList()
        }
    }

    private suspend fun handleActionLoadList() = coroutineScope {
        dispatch(ChatListStateChanges.ListChanged(buildDataSource(this)))
    }

    private fun buildDataSource(coroutineScope: CoroutineScope): PagedList<ChatListItem> {
        val factory = ChatListDataSourceFactory({ dispatch(it) }, chatRepository, coroutineScope.coroutineContext, retryChannel)

        return PagedList.Builder(factory.create(), pageListConfig)
            .setNotifyExecutor { coroutineScope.launch { it.run() } }
            .setFetchExecutor { coroutineScope.launch(Dispatchers.IO) { it.run() } }
            .build()
    }

}