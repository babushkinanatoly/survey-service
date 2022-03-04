package ru.babushkinanatoly.core

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [StringsModule::class]
)
interface StringsComponent : StringsProvider {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): StringsComponent
    }
}
