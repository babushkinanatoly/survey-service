package ru.babushkinanatoly.feature_profile.profile.di

import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineScope
import ru.babushkinanatoly.core.RepoProvider
import ru.babushkinanatoly.core.StringResProvider
import ru.babushkinanatoly.feature_profile.profile.ProfileModel

@ProfileScope
@Component(
    dependencies = [
        RepoProvider::class,
        StringResProvider::class
    ],
    modules = [ProfileModule::class]
)
internal interface ProfileComponent {

    fun provideModel(): ProfileModel

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance scope: CoroutineScope,
            stringResProvider: StringResProvider,
            repoProvider: RepoProvider,
        ): ProfileComponent
    }
}
