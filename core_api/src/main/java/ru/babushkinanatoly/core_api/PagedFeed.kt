package ru.babushkinanatoly.core_api

import kotlinx.coroutines.flow.StateFlow

interface PagedFeed {
    val status: StateFlow<Status>
    val feedEvent: Event<FeedEvent>
    fun refresh()
    fun loadMore(count: Int = 5, startAfter: String? = null)

    sealed class Status {
        data class Data(val surveys: List<Survey>?) : Status()
        data class Refreshing(val surveys: List<Survey>?) : Status()
        data class LoadingMore(val surveys: List<Survey>) : Status()
    }

    enum class FeedEvent {
        LOADING_MORE_ERROR, REFRESHING_ERROR
    }
}
