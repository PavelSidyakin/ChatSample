package com.example.chatsample.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.example.chatsample.auth.store.AuthIntentExecutor
import com.example.chatsample.auth.store.AuthIntentExecutorImpl
import com.example.chatsample.auth.store.AuthRepository
import com.example.chatsample.auth.store.AuthStoreFactory
import com.example.chatsample.auth.store.AuthStoreFactoryImpl
import com.example.chatsample.chatlist.store.data.ChatListDbRepository
import com.example.chatsample.chatlist.store.ChatListIntentExecutor
import com.example.chatsample.chatlist.store.ChatListIntentExecutorImpl
import com.example.chatsample.chatlist.store.ChatListStoreFactory
import com.example.chatsample.chatlist.store.ChatListStoreFactoryImpl
import com.example.chatsample.data.ChatDb
import com.example.chatsample.chatlist.store.recycler.ChatDataSourceImpl
import com.example.chatsample.data.ContextProvider
import com.example.chatsample.data.ContextProviderImpl
import com.example.chatsample.data.TelegramChatRepositoryImpl
import com.example.chatsample.chatlist.store.data.ChatListRemoteRepository
import com.example.chatsample.chatlist.store.recycler.ChatDataSource
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
    fun provideChatNetworkRepository(chatRepository: TelegramChatRepositoryImpl): ChatListRemoteRepository

    @Singleton
    @Binds
    fun provideContextProvider(contextProvider: ContextProviderImpl): ContextProvider

    @Singleton
    @Binds
    fun provideChatDataSource(chatRepository: ChatDataSourceImpl): ChatDataSource



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