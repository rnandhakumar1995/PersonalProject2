package io.nandha.personalproject2.repository

import android.app.Application
import androidx.room.Room

class RoomDb {
    companion object {
        private lateinit var instance: SongsDao
        fun getInstance(application: Application): SongsDao {
            if (!this::instance.isInitialized) {
                instance = Room.databaseBuilder(
                    application,
                    SongsDatabase::class.java, "songs"
                ).build().songsDao()
            }
            return instance
        }
    }
}