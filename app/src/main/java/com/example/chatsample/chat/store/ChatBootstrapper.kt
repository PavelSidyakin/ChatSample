package com.example.chatsample.chat.store

class ChatBootstrapper {

    sealed class Action {
        class LoadList: Action()
    }
}