package com.example.chatsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.chatsample.chat.view.ChatFragment
import com.example.chatsample.chatlist.controller.ChatListController
import com.example.chatsample.chatlist.view.ChatListFragment

class ChatMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ChatApplication.getAppComponent().inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentManager = supportFragmentManager
        fragmentManager.findFragmentByTag(ChatListFragment.FRAGMENT_TAG)?.let {
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.show(it)
            fragmentTransaction.commit()
        }?:let {
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.main_activity_container,
                createChatListFragment(), ChatListFragment.FRAGMENT_TAG
            //AuthFragment(), AuthFragment.FRAGMENT_TAG
            )
            fragmentTransaction.commit()
        }

        fragmentManager.executePendingTransactions()
    }

    private fun createChatListFragment(): Fragment {
        return ChatListFragment().apply {
            chatSelectedCallback = { chatListOutput ->
                when (chatListOutput) {
                    is ChatListController.Output.ChatSelected -> {
                        val fragmentTransaction = fragmentManager?.beginTransaction()
                        fragmentTransaction?.replace(R.id.main_activity_container,
                            ChatFragment().apply { setArguments(chatListOutput.chatId) }, ChatFragment.FRAGMENT_TAG
                        )
                        fragmentTransaction?.commit()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }

}