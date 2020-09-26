package com.example.chatsample.chat.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.arkivanov.mvikotlin.extensions.androidx.instancekeeper.getInstanceKeeperProvider
import com.arkivanov.mvikotlin.extensions.androidx.lifecycle.asMviLifecycle
import com.example.chatsample.ChatApplication
import com.example.chatsample.R
import com.example.chatsample.chat.controller.ChatController
import kotlinx.android.synthetic.main.chat_frament.chat_root_view

class ChatFragment : Fragment(){
    private val instanceKeeperProvider by lazy { getInstanceKeeperProvider() }

    private val controller: ChatController by lazy {
        ChatApplication.getAppComponent()
            .chatControllerFactory.create(instanceKeeperProvider)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.chat_frament, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
       controller.onViewCreated(ChatViewImpl(chat_root_view, lifecycle), lifecycle.asMviLifecycle())
    }

    companion object {
        val FRAGMENT_TAG = ChatFragment::class.qualifiedName
    }
}