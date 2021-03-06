package com.example.chatsample.chatlist.view

import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import com.example.chatsample.chatlist.store.ChatListStore
import com.example.chatsample.chatlist.view.recycler.ChatListAdapter
import com.example.chatsample.chatlist.view.recycler.ChatListClickListeners
import com.example.chatsample.chatlist.view.recycler.ChatListDelegationAdapter
import com.example.chatsample.chatlist.view.recycler.ChatListItem
import com.example.chatsample.chatlist.view.recycler.ChatListLoadStateAdapter
import com.example.chatsample.utils.diffPagingData
import kotlinx.android.synthetic.main.chat_list_frament.view.chat_list_list
import kotlinx.android.synthetic.main.chat_list_frament.view.chat_list_swipe_refresh

class ChatListViewImpl(
    private val rootView: View,
    private val lifecycle: Lifecycle,
) : BaseMviView<ChatListStore.State, ChatListStore.Intent>(), ChatListView {

    private val chatListClickListeners = object : ChatListClickListeners {
        override val directChatItemClickedListener: (ChatListItem.Chat.Direct) -> Unit = {
            dispatch(ChatListStore.Intent.OnChatSelected(it.chatId))
        }

        override val groupChatItemClickedListener: (ChatListItem.Chat.Group) -> Unit = {
            dispatch(ChatListStore.Intent.OnChatSelected(it.chatId))
        }
    }

    private val chatListAdapter = ChatListAdapter(chatListClickListeners)

    init {
        with(rootView) {
            rootView.chat_list_list.adapter = chatListAdapter
                .withLoadStateHeaderAndFooter(
                    ChatListLoadStateAdapter(
                        retry = {
                            dispatch(ChatListStore.Intent.OnRetry())
                        }
                    ),
                    ChatListLoadStateAdapter(
                        retry = {
                            dispatch(ChatListStore.Intent.OnRetry())
                        }
                    ))

            rootView.chat_list_swipe_refresh.setOnRefreshListener { dispatch(ChatListStore.Intent.OnRefresh()) }
        }

//        chatListAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
//            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
//                Log.i(TAG, "ChatListViewImpl.onItemRangeChanged(): positionStart = $positionStart, itemCount = $itemCount")
//                val pos = (rootView.chat_list_list.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()
//                Log.i(TAG, "ChatListViewImpl current pos: ${pos}")
//                pos?.let { position ->
//                    rootView.chat_list_list.scrollToPosition(pos)
//                }
//            }
//        })
    }

    override val renderer: ViewRenderer<ChatListStore.State> = diff {
        diffPagingData(
            get = ChatListStore.State::pagingData,
            set = { pagingData -> chatListAdapter.submitData(lifecycle, pagingData) }
        )

        diff(get = ChatListStore.State::isRefreshing, set = { refreshing ->
            rootView.chat_list_swipe_refresh.isRefreshing = refreshing
            if (refreshing) {
                chatListAdapter.refresh()
            }
        })

        diff(get = ChatListStore.State::isRetrying, set = { retrying ->
            if (retrying) {
                chatListAdapter.retry()
            }
        })
    }

    companion object {
        private const val TAG = "ChatListView"
    }

}