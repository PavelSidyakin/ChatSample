package com.example.chatsample.chat.view.recycler.delegates

import android.view.View
import com.example.chatsample.R
import com.example.chatsample.chat.model.MessageStatus
import com.example.chatsample.chat.view.recycler.MessageListItem
import com.example.chatsample.deleates_dsl.dsl.adapterLayoutContainer
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AdapterItemProvider
import kotlinx.android.synthetic.main.message_list_item_outgoing_message.view.message_list_outgoing_item_delivered
import kotlinx.android.synthetic.main.message_list_item_outgoing_message.view.message_list_outgoing_item_error
import kotlinx.android.synthetic.main.message_list_item_outgoing_message.view.message_list_outgoing_item_message_text
import kotlinx.android.synthetic.main.message_list_item_outgoing_message.view.message_list_outgoing_item_progress

fun outgoingMessageAdapterDelegate(retrySendClickedListener: (MessageListItem.Message.OutgoingMessage) -> Unit): AdapterDelegate<AdapterItemProvider<MessageListItem>> {
    return adapterLayoutContainer<MessageListItem.Message.OutgoingMessage, MessageListItem>(R.layout.message_list_item_outgoing_message) {

        itemView.message_list_outgoing_item_error.setOnClickListener { retrySendClickedListener(item) }

        bind {
            itemView.message_list_outgoing_item_message_text.text = item.text
            itemView.message_list_outgoing_item_progress.visibility = if (item.status == MessageStatus.SENDING) View.VISIBLE else View.INVISIBLE
            itemView.message_list_outgoing_item_delivered.visibility = if (item.status == MessageStatus.DELIVERED) View.VISIBLE else View.INVISIBLE
            itemView.message_list_outgoing_item_error.visibility = if (item.status == MessageStatus.ERROR) View.VISIBLE else View.INVISIBLE
        }
    }
}