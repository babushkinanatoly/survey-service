package ru.babushkinanatoly.core_impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.babushkinanatoly.core_api.MutableEvent
import ru.babushkinanatoly.core_api.PagedFeed
import ru.babushkinanatoly.core_api.dispatch
import ru.babushkinanatoly.core_impl.api.Api
import ru.babushkinanatoly.core_impl.api.RemoteException
import ru.babushkinanatoly.core_impl.db.Db

class PagedFeedImpl(
    private val surveysCount: Int = 10,
    private val scope: CoroutineScope,
    private val db: Db,
    private val api: Api,
) : PagedFeed {

    override val status = MutableStateFlow<PagedFeed.Status>(
        PagedFeed.Status.Refreshing(null)
    )

    override val feedEvent = MutableEvent<PagedFeed.FeedEvent>()

    override fun refresh() {
        status.update {
            when (it) {
                is PagedFeed.Status.Data -> PagedFeed.Status.Refreshing(it.surveys)
                is PagedFeed.Status.LoadingMore -> PagedFeed.Status.Refreshing(it.surveys)
                is PagedFeed.Status.Refreshing -> PagedFeed.Status.Refreshing(it.surveys)
            }
        }
        // TODO: Catch all exceptions wrapped in api
        scope.launch {
            try {
                val userVotes = db.getUserVotes()
                val surveys = api.getSurveys(surveysCount).map { remoteSurvey ->
                    remoteSurvey.toSurvey(
                        userVotes.find { it.surveyRemoteId == remoteSurvey.id }?.value
                    )
                }
                status.update { PagedFeed.Status.Data(surveys) }
            } catch (ex: RemoteException) {
                status.update { PagedFeed.Status.Data((it as PagedFeed.Status.Refreshing).surveys) }
                feedEvent.dispatch(PagedFeed.FeedEvent.REFRESHING_ERROR)
            }
        }
    }

    override fun loadMore(count: Int, startAfter: String?) {
        if (status.value is PagedFeed.Status.Data) {
            status.update { status ->
                when (status) {
                    is PagedFeed.Status.Data -> {
                        status.surveys?.let {
                            PagedFeed.Status.LoadingMore(it)
                        } ?: status
                    }
                    else -> error("Impossible PagedFeed status: $status")
                }
            }
            if (status.value is PagedFeed.Status.LoadingMore) {
                // TODO: Catch all exceptions wrapped in api
                scope.launch {
                    try {
                        val userVotes = db.getUserVotes()
                        val currentSurveys = (status.value as PagedFeed.Status.LoadingMore).surveys
                        val newSurveys = api.getSurveys(count, startAfter).map { remoteSurvey ->
                            remoteSurvey.toSurvey(
                                userVotes.find { it.surveyRemoteId == remoteSurvey.id }?.value
                            )
                        }
                        status.update { PagedFeed.Status.Data(currentSurveys + newSurveys) }
                    } catch (ex: RemoteException) {
                        status.update { PagedFeed.Status.Data((it as PagedFeed.Status.LoadingMore).surveys) }
                        feedEvent.dispatch(PagedFeed.FeedEvent.LOADING_MORE_ERROR)
                    }
                }
            }
        }
    }
}
