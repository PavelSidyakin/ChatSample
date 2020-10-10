package com.example.chatsample.chatlist.view.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatsample.R
import kotlinx.android.synthetic.main.chat_list_item.view.chat_list_item_name
import kotlinx.android.synthetic.main.chat_list_item.view.chat_list_item_type

class ChatListAdapter(val clickListeners: ChatListClickListeners) : PagingDataAdapter<ChatListItem, ChatListItemViewHolder>(ChatListDiffUtilItemCallback()) {

    override fun onBindViewHolder(holder: ChatListItemViewHolder, position: Int) {

        when (val item: ChatListItem? = getItem(position)) {
            is ChatListItem.Chat.Direct -> (holder as ChatListItemViewHolder.Direct).bind(item, clickListeners)
            is ChatListItem.Chat.Group -> (holder as ChatListItemViewHolder.Group).bind(item, clickListeners)
            else -> (holder as ChatListItemViewHolder.Placeholder).bind(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListItemViewHolder {

        return when (ChatListItemViewType.findByTypeId(viewType)) {
            ChatListItemViewType.DIRECT -> ChatListItemViewHolder.Direct.create(parent, LayoutInflater.from(parent.context))
            ChatListItemViewType.GROUP -> ChatListItemViewHolder.Group.create(parent, LayoutInflater.from(parent.context))
            ChatListItemViewType.PLACEHOLDER -> ChatListItemViewHolder.Placeholder.create(parent, LayoutInflater.from(parent.context))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ChatListItem.Chat.Direct -> ChatListItemViewType.DIRECT.typeId
            is ChatListItem.Chat.Group -> ChatListItemViewType.GROUP.typeId
            else -> ChatListItemViewType.PLACEHOLDER.typeId
        }
    }
}

private enum class ChatListItemViewType(val typeId: Int) {
    DIRECT(100),
    GROUP(200),
    PLACEHOLDER(300),
    ;

    companion object {
        fun findByTypeId(typeId: Int): ChatListItemViewType {
            return values().find { it.typeId == typeId }?:throw IllegalArgumentException("Unknown type: $typeId")
        }
    }
}

sealed class ChatListItemViewHolder(view: View): RecyclerView.ViewHolder(view) {

    class Direct(view: View): ChatListItemViewHolder(view) {
        fun bind(item: ChatListItem.Chat.Direct, clickListeners: ChatListClickListeners) {
            itemView.setOnClickListener { clickListeners.directChatItemClickedListener(item) }
            itemView.chat_list_item_type.text = itemView.context.getString(R.string.chat_type_direct)
            itemView.chat_list_item_name.text = item.name
        }

        companion object {
            fun create(parent: ViewGroup, inflater: LayoutInflater): Direct {
                return Direct(inflater.inflate(R.layout.chat_list_item, parent, false))
            }
        }
    }

    class Group(view: View): ChatListItemViewHolder(view) {
        fun bind(item: ChatListItem.Chat.Group, clickListeners: ChatListClickListeners) {
            itemView.setOnClickListener { clickListeners.groupChatItemClickedListener(item) }
            itemView.chat_list_item_type.text = itemView.context.getString(R.string.chat_type_group)
            itemView.chat_list_item_name.text = item.name
        }

        companion object {
            fun create(parent: ViewGroup, inflater: LayoutInflater): Group {
                return Group(inflater.inflate(R.layout.chat_list_item, parent, false))
            }
        }
    }

    class Placeholder(view: View): ChatListItemViewHolder(view) {
        fun bind(item: ChatListItem?) {
        }

        companion object {
            fun create(parent: ViewGroup, inflater: LayoutInflater): Placeholder {
                return Placeholder(inflater.inflate(R.layout.fallback_item_view, parent, false))
            }
        }
    }

}