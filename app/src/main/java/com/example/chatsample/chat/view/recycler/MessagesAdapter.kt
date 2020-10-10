package com.example.chatsample.chat.view.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatsample.R
import com.example.chatsample.chat.model.MessageStatus
import kotlinx.android.synthetic.main.message_list_item_incoming_message.view.message_list_incoming_item_message_text
import kotlinx.android.synthetic.main.message_list_item_incoming_message.view.message_list_incoming_item_sender_name
import kotlinx.android.synthetic.main.message_list_item_outgoing_message.view.message_list_outgoing_item_delivered
import kotlinx.android.synthetic.main.message_list_item_outgoing_message.view.message_list_outgoing_item_error
import kotlinx.android.synthetic.main.message_list_item_outgoing_message.view.message_list_outgoing_item_message_text
import kotlinx.android.synthetic.main.message_list_item_outgoing_message.view.message_list_outgoing_item_progress

class MessagesAdapter(val clickListeners: MessagesClickListeners) : PagingDataAdapter<MessageListItem, MessageListItemViewHolder>(
    MessageDiffUtilItemCallback()) {
    override fun onBindViewHolder(holder: MessageListItemViewHolder, position: Int) {

        when (val item: MessageListItem? = getItem(position)) {
            is MessageListItem.Message.OutgoingMessage -> (holder as MessageListItemViewHolder.Outgoing).bind(item, clickListeners)
            is MessageListItem.Message.IncomingMessage -> (holder as MessageListItemViewHolder.Incoming).bind(item, clickListeners)
            else -> (holder as MessageListItemViewHolder.Placeholder).bind(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageListItemViewHolder {

        return when (MessageListItemViewType.findByTypeId(viewType)) {
            MessageListItemViewType.INCOMING -> MessageListItemViewHolder.Incoming.create(parent, LayoutInflater.from(parent.context))
            MessageListItemViewType.OUTGOING -> MessageListItemViewHolder.Outgoing.create(parent, LayoutInflater.from(parent.context))
            MessageListItemViewType.PLACEHOLDER -> MessageListItemViewHolder.Placeholder.create(parent, LayoutInflater.from(parent.context))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MessageListItem.Message.IncomingMessage -> MessageListItemViewType.INCOMING.typeId
            is MessageListItem.Message.OutgoingMessage -> MessageListItemViewType.OUTGOING.typeId
            else -> MessageListItemViewType.PLACEHOLDER.typeId
        }
    }
}

private enum class MessageListItemViewType(val typeId: Int) {
    INCOMING(100),
    OUTGOING(200),
    PLACEHOLDER(300),
    ;

    companion object {
        fun findByTypeId(typeId: Int): MessageListItemViewType {
            return values().find { it.typeId == typeId }?:throw IllegalArgumentException("Unknown type: $typeId")
        }
    }
}

sealed class MessageListItemViewHolder(view: View): RecyclerView.ViewHolder(view) {

    class Incoming(view: View): MessageListItemViewHolder(view) {
        fun bind(item: MessageListItem.Message.IncomingMessage, clickListeners: MessagesClickListeners) {
            itemView.message_list_incoming_item_sender_name.text = item.from
            itemView.message_list_incoming_item_message_text.text = item.text
        }

        companion object {
            fun create(parent: ViewGroup, inflater: LayoutInflater): Incoming {
                return Incoming(inflater.inflate(R.layout.message_list_item_incoming_message, parent, false))
            }
        }
    }

    class Outgoing(view: View): MessageListItemViewHolder(view) {
        fun bind(item: MessageListItem.Message.OutgoingMessage, clickListeners: MessagesClickListeners) {
            itemView.message_list_outgoing_item_message_text.text = item.text
            itemView.message_list_outgoing_item_progress.visibility = if (item.status == MessageStatus.SENDING) View.VISIBLE else View.INVISIBLE
            itemView.message_list_outgoing_item_delivered.visibility = if (item.status == MessageStatus.DELIVERED) View.VISIBLE else View.INVISIBLE
            itemView.message_list_outgoing_item_error.visibility = if (item.status == MessageStatus.ERROR) View.VISIBLE else View.INVISIBLE
        }

        companion object {
            fun create(parent: ViewGroup, inflater: LayoutInflater): Outgoing {
                return Outgoing(inflater.inflate(R.layout.message_list_item_outgoing_message, parent, false))
            }
        }
    }

    class Placeholder(view: View): MessageListItemViewHolder(view) {
        fun bind(item: MessageListItem?) {
        }

        companion object {
            fun create(parent: ViewGroup, inflater: LayoutInflater): Placeholder {
                return Placeholder(inflater.inflate(R.layout.fallback_item_view, parent, false))
            }
        }
    }

}