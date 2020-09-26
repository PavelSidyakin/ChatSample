package com.example.chatsample.chat.view

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.paging.PagingData
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import com.example.chatsample.chat.store.ChatStore
import com.example.chatsample.chat.view.recycler.MessagesClickListeners
import com.example.chatsample.chat.view.recycler.MessagesLoadStateAdapter
import com.example.chatsample.chat.view.recycler.MessageItem
import com.example.chatsample.chat.view.recycler.MessagesDelegationAdapter
import com.example.chatsample.utils.diffPagingData
import kotlinx.android.synthetic.main.chat_frament.view.chat_message_list

class ChatViewImpl(
    private val rootView: View,
    private val lifecycle: Lifecycle,
) : BaseMviView<ChatStore.State, ChatStore.Intent>(), ChatView {

    private val messagesClickListeners = object : MessagesClickListeners {
        override val loadingItemClickListener: () -> Unit = {

        }
        override val onDeleteMessageClicked: (MessageItem.Message) -> Unit = {

        }
    }

    private val messagesAdapter = MessagesDelegationAdapter(messagesClickListeners)

    init {
        with(rootView) {
            rootView.chat_message_list.adapter = messagesAdapter
                .withLoadStateHeaderAndFooter(
                    MessagesLoadStateAdapter(
                        retry = {
                            dispatch(ChatStore.Intent.Retry())
                        }
                    ),
                    MessagesLoadStateAdapter(
                        retry = {
                            dispatch(ChatStore.Intent.Retry())
                        }
                    ))
        }
    }

    override val renderer: ViewRenderer<ChatStore.State> = diff {
        diffPagingData(
            get = ChatStore.State::pagingData,
            set = { pagingData -> messagesAdapter.submitData(lifecycle, pagingData) }
        )

        diff(get = ChatStore.State::pagingData, set = { pagingData ->
            pagingData?.let { pagingData: PagingData<MessageItem> ->
                messagesAdapter.submitData(lifecycle, pagingData)
            }
        }, compare = { _, _ -> false })

        diff(get = ChatStore.State::isRetrying, set = { retrying ->
            if (retrying) {
                messagesAdapter.retry()
            }
        })
    }

    companion object {
        private const val TAG = "ChatView"
    }

}