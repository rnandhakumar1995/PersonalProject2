package io.nandha.personalproject2.repository

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SongsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(songs: List<Song>)

    @Update
    fun update(song: Song): Int

    @Query("SELECT * FROM song")
    fun getAll(): Flow<List<Song>>
}