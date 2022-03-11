package ru.babushkinanatoly.core_impl.db

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.babushkinanatoly.core_impl.db.entity.UserEntity
import java.util.*

class DbImpl(context: Context) : Db {

    override fun getUser(): Flow<UserEntity?> = flow {
        if (Random().nextBoolean()) {
            emit(UserEntity(0, "Email", "User"))
        } else {
            emit(null)
        }
    }
}
