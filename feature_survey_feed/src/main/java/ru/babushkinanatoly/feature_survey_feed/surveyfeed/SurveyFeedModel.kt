package ru.babushkinanatoly.feature_survey_feed.surveyfeed

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.babushkinanatoly.core_api.*
import ru.babushkinanatoly.feature_survey_feed.R

internal interface SurveyFeedModel {
    val state: StateFlow<SurveyFeedState>
    val event: Event<FeedEvent>
    fun refresh()
    fun loadMore()
}

internal data class SurveyFeedState(
    val surveys: List<Survey>?,
    val refreshing: Boolean,
    val loadingMore: Boolean,
)

internal sealed class FeedEvent {
    data class Error(val msg: String) : FeedEvent()
}

internal class SurveyFeedModelImpl(
    scope: CoroutineScope,
    stringRes: StringRes,
    repo: Repo,
) : SurveyFeedModel {

    private val surveyFeed = repo.getSurveys(scope)

    override val state = MutableStateFlow(
        SurveyFeedState(
            surveys = listOf(),
            refreshing = true,
            loadingMore = false
        )
    )

    override val event = MutableEvent<FeedEvent>()

    init {
        refresh()
        scope.launch {
            surveyFeed.status.collect { status ->
                // TODO: For now, there is no check for corner cases
                when (status) {
                    is PagedFeed.Status.Data -> state.update {
                        it.copy(
                            surveys = status.surveys,
                            refreshing = false,
                            loadingMore = false
                        )
                    }
                    is PagedFeed.Status.LoadingMore -> state.update {
                        it.copy(
                            surveys = status.surveys,
                            refreshing = false,
                            loadingMore = true
                        )
                    }
                    is PagedFeed.Status.Refreshing -> state.update {
                        it.copy(
                            surveys = if (status.surveys == null) listOf() else status.surveys,
                            refreshing = true,
                            loadingMore = false
                        )
                    }
                }
            }
        }
        scope.launch {
            surveyFeed.feedEvent.consumeAsFlow().collect { feedEvent ->
                when (feedEvent) {
                    PagedFeed.FeedEvent.LOADING_MORE_ERROR -> {
                        event.dispatch(FeedEvent.Error(stringRes[R.string.error_loading_more]))
                    }
                    PagedFeed.FeedEvent.REFRESHING_ERROR -> {
                        state.value.surveys?.let {
                            event.dispatch(FeedEvent.Error(stringRes[R.string.error_refreshing]))
                        }
                    }
                }
            }
        }
    }

    override fun refresh() = surveyFeed.refresh()

    override fun loadMore() {
        state.value.surveys?.let {
            surveyFeed.loadMore(startAfter = it.lastOrNull()?.id)
        }
    }
}
