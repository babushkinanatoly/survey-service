package ru.babushkinanatoly.core_impl.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val email: String,
    val name: String,
    val age: Int,
    val sex: String,
    val countryCode: String,
)
