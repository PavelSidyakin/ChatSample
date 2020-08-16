package com.example.chatsample.chatlist.view

import android.view.View
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import com.example.chatsample.chatlist.store.ChatListStore
import com.example.chatsample.chatlist.view.recycler.ChatListAdapter
import com.example.chatsample.chatlist.view.recycler.ChatListClickListeners
import com.example.chatsample.chatlist.view.recycler.ChatListItem
import kotlinx.android.synthetic.main.chat_list_frament.view.chat_list_list
import kotlinx.android.synthetic.main.chat_list_frament.view.chat_list_progress

class ChatListViewImpl(private val rootView: View): BaseMviView<ChatListStore.State, ChatListStore.Intent>(), ChatListView {

    private val chatListClickListeners = object : ChatListClickListeners {
        override val directChatItemClickedListener: (ChatListItem.Direct) -> Unit = {

        }

        override val groupChatItemClickedListener: (ChatListItem.Group) -> Unit = {

        }
    }

    private val chatListAdapter = ChatListAdapter(chatListClickListeners)


    init {
        with(rootView) {
            rootView.chat_list_list.adapter = chatListAdapter
        }
    }

    override val renderer: ViewRenderer<ChatListStore.State> = diff {
        diff(get = ChatListStore.State::pagedList, set = {
            chatListAdapter.submitList(it)
        })

        diff(get = ChatListStore.State::isLoading, set = { loading ->
            rootView.chat_list_progress.visibility = if (loading) View.VISIBLE else View.INVISIBLE
        })
    }

}