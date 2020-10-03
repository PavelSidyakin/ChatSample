package com.example.chatsample.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.example.chatsample.auth.store.AuthIntentExecutor
import com.example.chatsample.auth.store.AuthIntentExecutorImpl
import com.example.chatsample.auth.store.AuthRepository
import com.example.chatsample.auth.store.AuthStoreFactory
import com.example.chatsample.auth.store.AuthStoreFactoryImpl
import com.example.chatsample.chat.store.ChatIntentExecutor
import com.example.chatsample.chat.store.ChatIntentExecutorImpl
import com.example.chatsample.chat.store.ChatStoreFactory
import com.example.chatsample.chat.store.ChatStoreFactoryImpl
import com.example.chatsample.chat.store.data.ChatDbRepository
import com.example.chatsample.chat.store.data.ChatRemoteRepository
import com.example.chatsample.data.database.ChatDb
import com.example.chatsample.chat.store.recycler.ChatDataSourceImpl
import com.example.chatsample.chatlist.store.ChatListIntentExecutor
import com.example.chatsample.chatlist.store.ChatListIntentExecutorImpl
import com.example.chatsample.chatlist.store.ChatListStoreFactory
import com.example.chatsample.chatlist.store.ChatListStoreFactoryImpl
import com.example.chatsample.chatlist.store.data.ChatListDbRepository
import com.example.chatsample.data.ContextProvider
import com.example.chatsample.data.ContextProviderImpl
import com.example.chatsample.data.TelegramChatRepositoryImpl
import com.example.chatsample.chatlist.store.data.ChatListRemoteRepository
import com.example.chatsample.chatlist.store.recycler.ChatListDataSource
import com.example.chatsample.data.ChatDbRepositoryImpl
import com.example.chatsample.data.ChatListDbRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface AppModule {

    @Singleton
    @Binds
    fun provideAuthRepository(chatRepository: TelegramChatRepositoryImpl): AuthRepository

    @Singleton
    @Binds
    fun provideChatRemoteRepository(chatRepository: TelegramChatRepositoryImpl): ChatRemoteRepository

    @Singleton
    @Binds
    fun provideChatNetworkRepository(chatRepository: TelegramChatRepositoryImpl): ChatListRemoteRepository

    @Singleton
    @Binds
    fun provideContextProvider(contextProvider: ContextProviderImpl): ContextProvider

    @Singleton
    @Binds
    fun provideChatDataSource(chatRepository: ChatDataSourceImpl): ChatListDataSource



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

    @Binds
    @Singleton
    fun provideChatListDbRepository(chatListDbRepository: ChatListDbRepositoryImpl): ChatListDbRepository

    // Chat. TODO: move to a separate component
    @Binds
    fun provideChatIntentExecutor(chatListIntentExecutor: ChatIntentExecutorImpl): ChatIntentExecutor

    @Binds
    fun provideChatStoreFactory(chatListIntentExecutor: ChatStoreFactoryImpl): ChatStoreFactory

    @Binds
    @Singleton
    fun provideChatDbRepository(chatListDbRepository: ChatDbRepositoryImpl): ChatDbRepository

    companion object {
        @Singleton
        @Provides
        fun provideStoreFactory(): StoreFactory {
            return DefaultStoreFactory
        }

        @Singleton
        @Provides
        fun provideChatDb(contextProvider: ContextProvider): ChatDb {
            return ChatDb.create(contextProvider.appContext)
        }
    }
}