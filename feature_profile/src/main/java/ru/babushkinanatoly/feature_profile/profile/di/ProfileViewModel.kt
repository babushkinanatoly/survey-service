package ru.babushkinanatoly.feature_profile.profile.di

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ru.babushkinanatoly.core.RepoProvider
import ru.babushkinanatoly.core.StringResProvider

internal class ProfileViewModel(app: Application) : AndroidViewModel(app) {

    val profileComponent = DaggerProfileComponent.factory()
        .create(viewModelScope, (app as StringResProvider), (app as RepoProvider))
}
