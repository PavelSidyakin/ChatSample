package com.example.chatsample.chat.model

enum class MessageStatus(val intValue: Int) {
    SENDING(1),
    DELIVERED(2),
    ;

    companion object {
        fun byIntValue(intValue: Int): MessageStatus {
            return MessageStatus.values().find { it.intValue == intValue } ?: throw RuntimeException("Wrong type intValue: $intValue")
        }
    }
}