package ru.babushkinanatoly.core

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [StringResModule::class]
)
interface StringResComponent : StringResProvider {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): StringResComponent
    }
}
