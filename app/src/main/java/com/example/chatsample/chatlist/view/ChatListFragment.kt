package com.example.chatsample.chatlist.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.arkivanov.mvikotlin.extensions.androidx.instancekeeper.getInstanceKeeperProvider
import com.arkivanov.mvikotlin.extensions.androidx.lifecycle.asMviLifecycle
import com.example.chatsample.ChatApplication
import com.example.chatsample.R
import com.example.chatsample.chatlist.controller.ChatListController
import kotlinx.android.synthetic.main.chat_list_frament.chat_list_root_view

class ChatListFragment : Fragment(){
    private val instanceKeeperProvider by lazy { getInstanceKeeperProvider() }

    private val controller: ChatListController by lazy {
        ChatApplication.getAppComponent()
            .chatListControllerFactory.create(instanceKeeperProvider)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.chat_list_frament, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
       controller.onViewCreated(ChatListViewImpl(chat_list_root_view, lifecycle), lifecycle.asMviLifecycle())
    }

    companion object {
        val FRAGMENT_TAG = ChatListFragment::class.qualifiedName
    }
}