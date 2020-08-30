package com.example.chatsample.chatlist.view

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import com.example.chatsample.chatlist.store.ChatListStore
import com.example.chatsample.chatlist.view.recycler.ChatListAdapter
import com.example.chatsample.chatlist.view.recycler.ChatListClickListeners
import com.example.chatsample.chatlist.view.recycler.ChatListItem
import com.example.chatsample.chatlist.view.recycler.ChatListItem2
import com.example.chatsample.chatlist.view.recycler.ChatListLoadStateAdapter
import kotlinx.android.synthetic.main.chat_list_frament.view.chat_list_list
import kotlinx.android.synthetic.main.chat_list_frament.view.chat_list_swipe_refresh

class ChatListViewImpl(
    private val rootView: View,
    private val lifecycle: Lifecycle,
    private val viewLifecycleOwner: LifecycleOwner
): BaseMviView<ChatListStore.State, ChatListStore.Intent>(), ChatListView {

    private val chatListClickListeners = object : ChatListClickListeners {
        override val directChatItemClickedListener: (ChatListItem.Direct) -> Unit = {

        }

        override val groupChatItemClickedListener: (ChatListItem.Group) -> Unit = {

        }
    }

    private val chatListAdapter = ChatListAdapter(chatListClickListeners)


    init {
        with(rootView) {
            rootView.chat_list_list.adapter = chatListAdapter.withLoadingFooter(ChatListLoadStateAdapter(chatListAdapter::retry))

//            chatListAdapter.addLoadStateListener { loadStates ->
//                footer.loadState = when (loadStates.refresh) {
//                    is LoadState.NotLoading -> loadStates.append
//                    else -> loadStates.refresh
//                }
//            }

            rootView.chat_list_swipe_refresh.setOnRefreshListener { dispatch(ChatListStore.Intent.Refresh()) }
        }
    }

    private fun ChatListAdapter.withLoadingFooter(
        footer: LoadStateAdapter<*>
    ): ConcatAdapter {
        addLoadStateListener { loadStates ->
            footer.loadState = when (loadStates.refresh) {
                is LoadState.NotLoading -> loadStates.append
                else -> loadStates.refresh
            }
        }
        return ConcatAdapter(this, footer)
    }

    override val renderer: ViewRenderer<ChatListStore.State> = diff {
        diff(get = ChatListStore.State::pagingData, set = { pagingData ->
            pagingData?.let { pagingData: PagingData<ChatListItem> ->
                chatListAdapter.submitData(lifecycle, pagingData)
            }
        }, compare = {_,_ -> false })

        diff(get = ChatListStore.State::isRefreshing, set = { refreshing ->
            rootView.chat_list_swipe_refresh.isRefreshing = refreshing
            if (refreshing) {
                chatListAdapter.refresh()
            }
        })

    }

}