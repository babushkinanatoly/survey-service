package ru.babushkinanatoly.core_impl.db

import kotlinx.coroutines.flow.Flow
import ru.babushkinanatoly.core_impl.db.entity.UserEntity

interface Db {
    fun getUser(): Flow<UserEntity?>
}
