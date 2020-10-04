package com.example.chatsample.chat.view.recycler.delegates

import com.example.chatsample.R
import com.example.chatsample.chat.view.recycler.MessageListItem
import com.example.chatsample.deleates_dsl.dsl.adapterLayoutContainer
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AdapterItemProvider
import kotlinx.android.synthetic.main.message_list_item_incoming_message.view.message_list_incoming_item_message_text
import kotlinx.android.synthetic.main.message_list_item_incoming_message.view.message_list_incoming_item_sender_name

fun incomingMessageAdapterDelegate(): AdapterDelegate<AdapterItemProvider<MessageListItem>> {
    return adapterLayoutContainer<MessageListItem.Message.IncomingMessage, MessageListItem>(R.layout.message_list_item_incoming_message) {

//        itemView.setOnClickListener { itemClickedListener(item) }

        bind {
            itemView.message_list_incoming_item_sender_name.text = item.from
            itemView.message_list_incoming_item_message_text.text = item.text
        }
    }
}