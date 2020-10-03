package com.example.chatsample.chat.store

class ChatBootstrapper {

    sealed class Action {
        class LoadMessageList(val chatId: Long) : Action()
    }
}