package com.example.chatsample.chatlist.store

class ChatListBootstrapper {

    sealed class Action {
        class LoadList: Action()
    }
}