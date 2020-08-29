package com.example.chatsample.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.example.chatsample.auth.store.AuthIntentExecutor
import com.example.chatsample.auth.store.AuthIntentExecutorImpl
import com.example.chatsample.auth.store.AuthStoreFactory
import com.example.chatsample.auth.store.AuthStoreFactoryImpl
import com.example.chatsample.chatlist.store.ChatListIntentExecutor
import com.example.chatsample.chatlist.store.ChatListIntentExecutorImpl
import com.example.chatsample.chatlist.store.ChatListStoreFactory
import com.example.chatsample.chatlist.store.ChatListStoreFactoryImpl
import com.example.chatsample.data.ContextProvider
import com.example.chatsample.data.ContextProviderImpl
import com.example.chatsample.data.TelegramChatRepositoryImpl
import com.example.chatsample.chatlist.store.repository.ChatNetworkRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface AppModule {

    @Singleton
    @Binds
    fun provideChatRepository(chatRepository: TelegramChatRepositoryImpl): ChatNetworkRepository

    @Singleton
    @Binds
    fun provideContextProvider(contextProvider: ContextProviderImpl): ContextProvider

    // Auth. TODO: move to a separate component
    @Binds
    fun provideAuthIntentExecutor(authIntentExecutor: AuthIntentExecutorImpl): AuthIntentExecutor

    @Binds
    fun provideAuthStoreFactory(authIntentExecutor: AuthStoreFactoryImpl): AuthStoreFactory

    // ChatList. TODO: move to a separate component
    @Binds
    fun provideChatListIntentExecutor(chatListIntentExecutor: ChatListIntentExecutorImpl): ChatListIntentExecutor

    @Binds
    fun provideChatListStoreFactory(chatListIntentExecutor: ChatListStoreFactoryImpl): ChatListStoreFactory

    companion object {
        @Singleton
        @Provides
        fun provideStoreFactory(): StoreFactory {
            return DefaultStoreFactory
        }
    }
}