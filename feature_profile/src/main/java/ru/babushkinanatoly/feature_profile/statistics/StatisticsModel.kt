package ru.babushkinanatoly.feature_profile.statistics

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import ru.babushkinanatoly.core_api.Repo
import ru.babushkinanatoly.core_api.StringRes

internal interface StatisticsModel {
    val state: StateFlow<StatisticsState>
}

internal data class StatisticsState(
    val surveysCount: Int,
    val upvotesCount: Int,
    val downvotesCount: Int,
)

internal class StatisticsModelImpl(
    scope: CoroutineScope,
    stringRes: StringRes,
    repo: Repo,
) : StatisticsModel {

    override val state = repo.getUserSurveys().combine(repo.getUserVotes()) { surveys, votes ->
        StatisticsState(
            surveysCount = surveys.size,
            upvotesCount = votes.count { it },
            downvotesCount = votes.count { !it }
        )
    }.stateIn(
        scope,
        SharingStarted.WhileSubscribed(),
        StatisticsState(
            surveysCount = 0,
            upvotesCount = 0,
            downvotesCount = 0
        )
    )
}
