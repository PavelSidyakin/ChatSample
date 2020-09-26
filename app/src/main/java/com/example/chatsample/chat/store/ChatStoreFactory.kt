package com.example.chatsample.chat.store

interface ChatStoreFactory {
    fun create(): ChatStore
}