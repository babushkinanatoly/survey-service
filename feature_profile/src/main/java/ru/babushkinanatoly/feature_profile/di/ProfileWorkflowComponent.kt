package ru.babushkinanatoly.feature_profile.di

import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineScope
import ru.babushkinanatoly.core.RepoProvider
import ru.babushkinanatoly.core.StringResProvider
import ru.babushkinanatoly.feature_profile.ProfileWorkflowModel

@ProfileWorkflowScope
@Component(
    dependencies = [
        RepoProvider::class,
        StringResProvider::class
    ],
    modules = [ProfileWorkflowModule::class]
)
internal interface ProfileWorkflowComponent {

    fun provideModel(): ProfileWorkflowModel

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance scope: CoroutineScope,
            stringResProvider: StringResProvider,
            repoProvider: RepoProvider,
        ): ProfileWorkflowComponent
    }
}
