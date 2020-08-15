package com.example.chatsample.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.example.chatsample.auth.store.AuthIntentExecutor
import com.example.chatsample.auth.store.AuthIntentExecutorImpl
import com.example.chatsample.auth.store.AuthStoreFactory
import com.example.chatsample.auth.store.AuthStoreFactoryImpl
import com.example.chatsample.data.ContextProvider
import com.example.chatsample.data.ContextProviderImpl
import com.example.chatsample.data.TelegramChatRepositoryImpl
import com.example.chatsample.data.ChatRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface AppModule {

    @Singleton
    @Binds
    fun provideChatRepository(chatRepository: TelegramChatRepositoryImpl): ChatRepository

    @Singleton
    @Binds
    fun provideContextProvider(contextProvider: ContextProviderImpl): ContextProvider

    @Binds
    fun provideAuthIntentExecutor(authIntentExecutor: AuthIntentExecutorImpl): AuthIntentExecutor

    @Binds
    fun provideAuthStoreFactory(authIntentExecutor: AuthStoreFactoryImpl): AuthStoreFactory

    companion object {
        @Singleton
        @Provides
        fun provideStoreFactory(): StoreFactory {
            return DefaultStoreFactory
        }
    }
}