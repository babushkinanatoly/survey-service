package ru.babushkinanatoly.feature_auth.di

import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineScope
import ru.babushkinanatoly.core.RepoProvider
import ru.babushkinanatoly.core.StringsProvider
import ru.babushkinanatoly.feature_auth.AuthModel

@AuthScope
@Component(
    dependencies = [
        RepoProvider::class,
        StringsProvider::class
    ],
    modules = [AuthModule::class]
)
interface AuthComponent {

    fun provideModel(): AuthModel

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance scope: CoroutineScope,
            stringsProvider: StringsProvider,
            repoProvider: RepoProvider,
        ): AuthComponent
    }
}
