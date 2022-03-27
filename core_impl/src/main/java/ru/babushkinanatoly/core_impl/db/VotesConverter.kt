package ru.babushkinanatoly.core_impl.db

import androidx.room.TypeConverter

class VotesConverter {

    companion object {
        private const val SEPARATOR = "|||"
    }

    @TypeConverter
    fun votesToString(votedUserIds: List<String>) = votedUserIds.joinToString(separator = SEPARATOR)

    @TypeConverter
    fun stringToVotes(votedUserIds: String) =
        if (votedUserIds.isEmpty()) listOf() else votedUserIds.split(SEPARATOR)
}
