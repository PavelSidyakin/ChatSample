package com.example.chatsample.utils

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState

fun <Key: Any, Value: Any, NewValue: Any> PagingSource<Key, Value>.mapValue(
    converterOldToNew: (Value) -> NewValue,
    converterNewToOld: (NewValue) -> Value,
): PagingSource<Key, NewValue> {
    return NewPagingSource({ this }, converterOldToNew, converterNewToOld)
}

private class NewPagingSource<Key : Any, Value : Any, NewValue: Any>(
    private val oldDataSourceFactory: () -> PagingSource<Key, Value>,
    private val converterOldToNew: (Value) -> NewValue,
    private val converterNewToOld: (NewValue) -> Value,
) : PagingSource<Key, NewValue>() {
    private val dataSource: PagingSource<Key, Value> by lazy {
        oldDataSourceFactory().also { dataSource ->
            dataSource.registerInvalidatedCallback(::invalidate)
            if (dataSource.invalid && !invalid) {
                dataSource.unregisterInvalidatedCallback(::invalidate)
                super.invalidate()
            }
        }
    }

    override suspend fun load(params: LoadParams<Key>): LoadResult<Key, NewValue> {
        return dataSource.load(params).let { loadResult: LoadResult<Key, Value> ->
            when (loadResult) {
                is LoadResult.Error -> LoadResult.Error<Key, NewValue>(loadResult.throwable)
                is LoadResult.Page -> LoadResult.Page<Key, NewValue>(
                    loadResult.data.map { converterOldToNew(it) },
                    loadResult.prevKey,
                    loadResult.nextKey,
                    loadResult.itemsBefore,
                    loadResult.itemsAfter
                )
            }
        }
    }

    override fun invalidate() {
        super.invalidate()
        dataSource.invalidate()
    }

    @ExperimentalPagingApi
    override fun getRefreshKey(state: PagingState<Key, NewValue>): Key? {
        return state.anchorPosition as Key

//        return dataSource.getRefreshKey(
//            PagingState(
//                pages = state.pages.map { loadResult ->
//                    LoadResult.Page<Key, Value>(
//                        loadResult.data.map { converterNewToOld(it) },
//                        loadResult.prevKey,
//                        loadResult.nextKey,
//                        loadResult.itemsBefore,
//                        loadResult.itemsAfter
//                    )
//                },
//                anchorPosition = state.anchorPosition,
//                config = state.config,
//                leadingPlaceholderCount = 0
//            )
//        )
    }

    override val jumpingSupported: Boolean
        get() = dataSource.jumpingSupported

    override val keyReuseSupported: Boolean
        get() = dataSource.keyReuseSupported
}
