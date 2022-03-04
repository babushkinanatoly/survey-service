package ru.babushkinanatoly.core

import ru.babushkinanatoly.core_api.Repo

interface RepoProvider {
    fun provideRepo(): Repo
}
