package ru.babushkinanatoly.core

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [RepoModule::class]
)
interface RepoComponent : RepoProvider {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): RepoComponent
    }
}
