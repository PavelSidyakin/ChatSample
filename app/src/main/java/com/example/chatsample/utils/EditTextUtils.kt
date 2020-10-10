package com.example.chatsample.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

fun EditText.setTextCompat(text: CharSequence, textWatcher: TextWatcher? = null) {
    val savedSelectionStart = selectionStart
    val savedSelectionEnd = selectionEnd
    textWatcher?.also(::removeTextChangedListener)
    setText(text)
    textWatcher?.also(::addTextChangedListener)
    if (savedSelectionEnd <= text.length) {
        setSelection(savedSelectionStart, savedSelectionEnd)
    } else {
        setSelection(text.length)
    }
}

open class SimpleTextWatcher : TextWatcher {
    override fun afterTextChanged(s: Editable) {
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    }
}
