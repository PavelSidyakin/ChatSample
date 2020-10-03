package com.example.chatsample.chat.view

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.arkivanov.mvikotlin.extensions.androidx.instancekeeper.getInstanceKeeperProvider
import com.arkivanov.mvikotlin.extensions.androidx.lifecycle.asMviLifecycle
import com.example.chatsample.ChatApplication
import com.example.chatsample.R
import com.example.chatsample.chat.controller.ChatController
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.chat_frament.chat_root_view

class ChatFragment : Fragment(){
    private val instanceKeeperProvider by lazy { getInstanceKeeperProvider() }

    private val args: Arguments by lazy { requireNotNull(requireArguments().getParcelable(KEY_ARGUMENTS)) }

    private val controller: ChatController by lazy {
        ChatApplication.getAppComponent()
            .chatControllerFactory.create(
                instanceKeeperProvider,
                dependencies = object: ChatController.Dependencies {
                    override val chatId: Long = args.chatId
                }
            )
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

    fun setArguments(chatId: Long) {
        arguments = bundleOf(KEY_ARGUMENTS to Arguments(chatId = chatId))
    }

    @Parcelize
    private class Arguments(
        val chatId: Long
    ) : Parcelable

    companion object {
        val FRAGMENT_TAG = ChatFragment::class.qualifiedName
        private const val KEY_ARGUMENTS = "ARGUMENTS"
    }
}