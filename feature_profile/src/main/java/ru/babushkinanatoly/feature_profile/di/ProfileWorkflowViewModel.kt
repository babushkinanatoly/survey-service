package ru.babushkinanatoly.feature_profile.di

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ru.babushkinanatoly.core.RepoProvider
import ru.babushkinanatoly.core.StringResProvider

internal class ProfileWorkflowViewModel(app: Application) : AndroidViewModel(app) {

    val profileWorkflowComponent = DaggerProfileWorkflowComponent.factory()
        .create(viewModelScope, (app as StringResProvider), (app as RepoProvider))
}
