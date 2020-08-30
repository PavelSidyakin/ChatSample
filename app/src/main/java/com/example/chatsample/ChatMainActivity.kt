package com.example.chatsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chatsample.auth.view.AuthFragment
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
                ChatListFragment(), ChatListFragment.FRAGMENT_TAG
            //AuthFragment(), AuthFragment.FRAGMENT_TAG
            )
            fragmentTransaction.commit()
        }

        fragmentManager.executePendingTransactions()
    }

    override fun onBackPressed() {
        finish()
    }

}