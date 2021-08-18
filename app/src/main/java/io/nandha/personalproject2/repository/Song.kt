package io.nandha.personalproject2.repository

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Song(
    @PrimaryKey val id: String,
    val name: String,
    val artistName: String,
    var isLiked: Boolean = false
)
