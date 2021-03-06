package com.example.chatsample.chat.view

import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import com.example.chatsample.chat.store.ChatStore
import com.example.chatsample.chat.view.recycler.MessagesClickListeners
import com.example.chatsample.chat.view.recycler.MessagesLoadStateAdapter
import com.example.chatsample.chat.view.recycler.MessageListItem
import com.example.chatsample.chat.view.recycler.MessagesAdapter
import com.example.chatsample.chat.view.recycler.MessagesDelegationAdapter
import com.example.chatsample.utils.SimpleTextWatcher
import com.example.chatsample.utils.diffPagingData
import com.example.chatsample.utils.setTextCompat
import kotlinx.android.synthetic.main.chat_frament.view.chat_message_edit_text
import kotlinx.android.synthetic.main.chat_frament.view.chat_message_list
import kotlinx.android.synthetic.main.chat_frament.view.chat_send_button

class ChatViewImpl(
    private val rootView: View,
    private val lifecycle: Lifecycle,
) : BaseMviView<ChatStore.State, ChatStore.Intent>(), ChatView {

    private val messagesClickListeners = object : MessagesClickListeners {
        override val onRetrySendClicked: (MessageListItem.Message.OutgoingMessage) -> Unit = {
            TODO("Not implemented")
        }
    }

    private val messagesAdapter = MessagesAdapter(messagesClickListeners)

    private val textWatcher =
        object : SimpleTextWatcher() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                dispatch(ChatStore.Intent.OutgoingMessageText(s.toString()))
            }
        }

    init {
        with(rootView) {
            chat_message_list.adapter = messagesAdapter
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

            chat_send_button.setOnClickListener {
                dispatch(ChatStore.Intent.SendMessage())
            }

            chat_message_edit_text.addTextChangedListener(textWatcher)
        }

        messagesAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0 && itemCount == 1) { // One new message was added.
                    rootView.chat_message_list.scrollToPosition(0)
                }
            }
        })
    }

    override val renderer: ViewRenderer<ChatStore.State> = diff {
        diffPagingData(
            get = ChatStore.State::pagingData,
            set = { pagingData -> messagesAdapter.submitData(lifecycle, pagingData) }
        )
        diff(get = ChatStore.State::isRetrying, set = { retrying ->
            if (retrying) {
                messagesAdapter.retry()
            }
        })
        diff(get = ChatStore.State::currentlyEditingMessage, set = { currentlyEditingMessage ->
            rootView.chat_message_edit_text.setTextCompat(currentlyEditingMessage, textWatcher)
        })
    }

    companion object {
        private const val TAG = "ChatView"
    }

}