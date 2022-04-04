package ru.babushkinanatoly.feature_profile.statistics.di

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ru.babushkinanatoly.core.RepoProvider
import ru.babushkinanatoly.core.StringResProvider

internal class StatisticsViewModel(app: Application) : AndroidViewModel(app) {

    val statisticsComponent = DaggerStatisticsComponent.factory()
        .create(viewModelScope, (app as StringResProvider), (app as RepoProvider))
}
