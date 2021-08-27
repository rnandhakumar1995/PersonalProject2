package io.nandha.personalproject2.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.nandha.personalproject2.repository.RoomDb
import io.nandha.personalproject2.repository.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MainActivityViewModel(private val app: Application) : AndroidViewModel(app) {

    val songs = MutableSharedFlow<List<Song>>()
    private val roomDb = RoomDb.getInstance(app)

    fun initializeLocalDb() {
        viewModelScope.launch(Dispatchers.IO) {
            val readLocalFiles = readLocalFiles()
            insertLocalFilesToDb(readLocalFiles)
        }
    }

    private fun insertLocalFilesToDb(songs: List<Song>) {
        roomDb.insertAll(songs)
    }

    private fun readLocalFiles(): ArrayList<Song> {
        val cr: ContentResolver = app.contentResolver
        val uri: Uri = MediaStore.Files.getContentUri("external")
        val projection: Array<String> = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Media.DATA
        )
        val sortOrder: String? = null
        val selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?"
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3")
        val selectionArgsMp3 = arrayOf(mimeType)
        val allMp3Files =
            cr.query(uri, projection, selectionMimeType, selectionArgsMp3, sortOrder)
        return mapCursorToSongList(allMp3Files)
    }

    private fun mapCursorToSongList(allMp3Files: Cursor?): ArrayList<Song> {
        val songs = arrayListOf<Song>()
        allMp3Files?.let {
            try {
                if (it.moveToFirst()) {
                    while (allMp3Files.moveToNext()) {
                        println("Path ${getAlbumDetail(it, MediaStore.Audio.Media.DATA)}")
                        val song = Song(
                            getAlbumDetail(it, MediaStore.Audio.Media._ID),
                            getAlbumDetail(it, MediaStore.Audio.Media.DISPLAY_NAME),
                            getAlbumDetail(it, MediaStore.Audio.Albums.ARTIST),
                            getAlbumDetail(it, MediaStore.Audio.Media.DATA)
                        )
                        songs.add(song)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                allMp3Files.close()
            }
        }
        return songs
    }

    private fun getAlbumDetail(it: Cursor, key: String) =
        it.getString(it.getColumnIndex(key))

    fun readDbFiles() {
        viewModelScope.launch {
            roomDb.getAll().collectLatest {
                songs.emit(it)
            }
        }
    }

    fun toggleLike(song: Song) {
        viewModelScope.launch(Dispatchers.IO) {
            roomDb.update(song)
        }
    }
}