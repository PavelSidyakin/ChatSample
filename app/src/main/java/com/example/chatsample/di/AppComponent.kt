package com.example.chatsample.di

import com.example.chatsample.ChatApplication
import com.example.chatsample.ChatMainActivity
import com.example.chatsample.auth.controller.AuthControllerImpl
import com.example.chatsample.auth.controller.AuthControllerImpl_AssistedFactory
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class, AppAssistedModule::class])
@Singleton
interface AppComponent {
    fun inject(theApplication: ChatApplication)
    fun inject(theApplication: ChatMainActivity)

    val authControllerFactory: AuthControllerImpl.Factory

    interface Builder {
        fun build(): AppComponent
    }
}