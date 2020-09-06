package com.example.chatsample.chatlist.store

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.flatMap
import androidx.paging.insertSeparators
import androidx.paging.map
import com.arkivanov.mvikotlin.extensions.coroutines.SuspendExecutor
import com.example.chatsample.chatlist.view.recycler.ChatListItem
import com.example.chatsample.model.ChatInfo
import com.example.chatsample.model.ChatType
import com.example.chatsample.chatlist.store.paging.ChatDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

private val job: Job = Job()

@InternalCoroutinesApi
class ChatListIntentExecutorImpl @Inject constructor(
    private val chatDataSource: ChatDataSource,
) : SuspendExecutor<ChatListStore.Intent, ChatListBootstrapper.Action, ChatListStore.State, ChatListStateChanges, ChatListStore.Label>(
    mainContext = job + Dispatchers.IO
), ChatListIntentExecutor {

    init {
        Log.i("Executor", "Init")
        job.invokeOnCompletion(true) {
            Log.i("Executor", "completion")
        }
    }

    override suspend fun executeAction(action: ChatListBootstrapper.Action, getState: () -> ChatListStore.State) {
        when (action) {
            is ChatListBootstrapper.Action.LoadList -> handleActionLoadList()
        }
    }

    override suspend fun executeIntent(intent: ChatListStore.Intent, getState: () -> ChatListStore.State) {
        when (intent) {
            is ChatListStore.Intent.Refresh -> {
                dispatchOnMainThread(ChatListStateChanges.RefreshStateChanged(true))
                //handleActionLoadList()
            }
            //is ChatListStore.Intent.LoadingState -> loadingStateChannel.send(intent.isLoading)
        }
    }

//    private suspend fun handleActionLoadList() = coroutineScope {
//        chatDataSource.observeChatList()
//            .map { pagingData -> pagingData.map { convertChatInfo2ChatListItem(it) } }
////            .combine(chatDataSource.observeLoadingState()) { pagingData: PagingData<ChatListItem>, isLoading: Boolean ->
////                pagingData to isLoading
////            }
////            .map { pagingDataToIsLoadingPair ->
////                val pagingData = pagingDataToIsLoadingPair.first
////                val isLoading = pagingDataToIsLoadingPair.second
////
////                if (isLoading) {
////                    pagingData.insertFooterItem(ChatListItem.Loading)
////                }
////
////                pagingData
////            }
//            .cachedIn(this)
//            .collectLatest { pagingData ->
//                  val isLoading = chatDataSource.observeLoadingState().receive()
//
//                    val pagingWithFooterIfNeeded = if (isLoading) {
//                        Log.i("ChatList", "Inserting footer")
//                        pagingData.insertFooterItem(ChatListItem.Loading)
//                    } else {
//                        Log.i("ChatList", "Isn't inserting footer")
//                        pagingData
//                    }
//                    dispatch(ChatListStateChanges.ListChanged(pagingWithFooterIfNeeded))
//                    dispatch(ChatListStateChanges.RefreshStateChanged(false))
//
//            }
//    }

    override fun dispose() {
        super.dispose()
    }

    private suspend fun handleActionLoadList() = coroutineScope {
        //loadingStateChannel.send(false)

        chatDataSource.observeChatList(this)
            .cachedIn(this)
            .map { pagingData -> pagingData.map { convertChatInfo2ChatListItem(it) } }
            .combine(chatDataSource.observeLoadingState().receiveAsFlow()) { pagingData: PagingData<ChatListItem>, isLoading: Boolean ->
                pagingData to isLoading
            }
            .map { pagingDataToIsLoadingPair ->
                val pagingData = pagingDataToIsLoadingPair.first
                val isLoading = pagingDataToIsLoadingPair.second

//                val pagingWithFooterIfNeeded = if (isLoading) {
//                    Log.i("ChatList", "Inserting footer")
//                    pagingData.insertFooterItem(ChatListItem.Loading())
//                        .insertFooterItem(ChatListItem.Loading())
//                    pagingData.insertSeparators { chatListItem, chatListItem2 ->
//                        ChatListItem.Loading()
//                    }
                    pagingData
//                        .filter { chatListItem ->
//                        if (chatListItem is ChatListItem.Loading) {
//                            isLoading
//                        } else {
//                            true
//                        }
                    //}
//                } else {
//                    Log.i("ChatList", "Isn't inserting footer")
//                    pagingData
//                }
//
//                pagingWithFooterIfNeeded
            }
            .collectLatest { pagingData ->
                dispatchOnMainThread(ChatListStateChanges.ListChanged(pagingData))
                dispatchOnMainThread(ChatListStateChanges.RefreshStateChanged(false))
            }
    }

    private suspend fun dispatchOnMainThread(changes: ChatListStateChanges) {
        withContext(Dispatchers.Main) {
            dispatch(changes)
        }
    }

    private fun convertChatInfo2ChatListItem(chatInfo: ChatInfo): ChatListItem {
        return when (chatInfo.chatType) {
            ChatType.DIRECT -> ChatListItem.Chat.Direct(chatInfo.chatId, chatInfo.chatName)
            ChatType.GROUP -> ChatListItem.Chat.Group(chatInfo.chatId, chatInfo.chatName)
            ChatType.LOADING -> ChatListItem.Loading()
        }
    }

}