package io.nandha.personalproject2.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.nandha.personalproject2.R
import io.nandha.personalproject2.repository.Song

class SongsAdapter(val toggleLike: (song: Song) -> Unit) :
    RecyclerView.Adapter<SongsAdapter.ViewHolder>() {
    private var data: List<Song> = arrayListOf()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.name)
        var artist: TextView = itemView.findViewById(R.id.artist)
        var liked: TextView = itemView.findViewById(R.id.liked)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = data[position]
        holder.name.text = song.name
        holder.artist.text = song.artistName
        holder.liked.text = if(song.isLiked) "Unlike" else "Like"
        holder.liked.setOnClickListener {
            song.isLiked = !song.isLiked
            toggleLike(song)
        }
    }

    override fun getItemCount() = data.size

    fun setData(data: List<Song>) {
        this.data = data
        notifyDataSetChanged()
    }
}