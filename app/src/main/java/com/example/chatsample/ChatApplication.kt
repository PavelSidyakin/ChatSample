package com.example.chatsample

import android.app.Application
import com.example.chatsample.data.ContextProvider
import com.example.chatsample.di.AppComponent
import com.example.chatsample.di.DaggerAppComponent
import com.example.chatsample.chatlist.store.recycler.ChatNetworkRepository
import javax.inject.Inject

class ChatApplication : Application() {

    @Inject
    lateinit var contextProvider: ContextProvider

    @Inject
    lateinit var chatNetworkRepository: ChatNetworkRepository

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .build()

        appComponent.inject(this)
        contextProvider.appContext = this

        chatNetworkRepository.init()

    }

    companion object {
        private lateinit var appComponent: AppComponent

        fun getAppComponent(): AppComponent {
            return appComponent
        }
    }}