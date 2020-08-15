package com.example.chatsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatsample.auth.view.AuthFragment

class ChatMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ChatApplication.getAppComponent().inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentManager = supportFragmentManager
        fragmentManager.findFragmentByTag(AuthFragment.FRAGMENT_TAG)?.let {
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.show(it)
            fragmentTransaction.commit()
        }?:let {
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.main_activity_container,
                AuthFragment(), AuthFragment.FRAGMENT_TAG
            )
            fragmentTransaction.commit()
        }

        fragmentManager.executePendingTransactions()
    }}