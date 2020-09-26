package com.example.chatsample.utils

import androidx.paging.PagingData
import com.arkivanov.mvikotlin.core.utils.DiffBuilder

fun <Model: Any, PM, PD: PagingData<PM>> DiffBuilder<Model>.diffPagingData(
    get: (Model) -> PD?,
    set: (PD) -> Unit
) {
    diff(
        get = get,
        set = { pd: PD? -> pd?.let { set(it) } },
        compare = { _, _ -> false } // Always let the adapter to compare
    )
}