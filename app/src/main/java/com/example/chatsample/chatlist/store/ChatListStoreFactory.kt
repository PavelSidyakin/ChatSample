package com.example.chatsample.chatlist.store

interface ChatListStoreFactory {
    fun create(): ChatListStore
}