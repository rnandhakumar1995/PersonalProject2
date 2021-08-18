package io.nandha.personalproject2.repository

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Song::class], version = 1)
abstract class SongsDatabase : RoomDatabase() {
    abstract fun songsDao(): SongsDao
}