package com.notes.notesproxmlviews

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class NoteAdapter(
    options: FirestoreRecyclerOptions<Note>, val context: Context
) : FirestoreRecyclerAdapter<Note, NoteAdapter.NoteViewHolder>(options) {

    override fun onBindViewHolder(
        holder: NoteViewHolder, position: Int, note: Note
    ) {
        holder.titleTextView.text = note.title
        holder.contentTextView.text = note.content
        holder.timestampTextView.text = Utility.timestampToString(note.timestamp)

        if (note.imageBase64 != null && note.imageBase64!!.isNotEmpty()) {
            try {
                val decodedString = android.util.Base64.decode(note.imageBase64, android.util.Base64.DEFAULT)
                val decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                holder.thumbnailImageView.setImageBitmap(decodedByte)
                holder.thumbnailImageView.visibility = View.VISIBLE
            } catch (e: Exception) {
                holder.thumbnailImageView.visibility = View.GONE
            }
        } else {
            holder.thumbnailImageView.visibility = View.GONE
        }

        if (note.tags != null && note.tags!!.isNotEmpty()) {
            val tagsString = note.tags!!.joinToString(" ") { "#$it" }
            holder.tagsTextView.text = tagsString
            holder.tagsTextView.visibility = View.VISIBLE
        } else {
            holder.tagsTextView.visibility = View.GONE
        }


        if (note.noteColor != null && note.noteColor!!.isNotEmpty()) {
            try {
                holder.itemView.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor(note.noteColor))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            holder.itemView.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, NoteDetailsActivity::class.java)
            intent.putExtra("title", note.title)
            intent.putExtra("content", note.content)
            intent.putExtra("imageBase64", note.imageBase64)
            intent.putExtra("noteColor", note.noteColor ?: "#FFFFFF")
            intent.putStringArrayListExtra("tags", if (note.tags != null) ArrayList(note.tags) else null)
            val docId = this.snapshots.getSnapshot(position).id
            intent.putExtra("docId", docId)

            context.startActivity(intent)
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): NoteViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_note_item, parent, false)

        return NoteViewHolder(view)
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val titleTextView: TextView = itemView.findViewById(R.id.note_title_text_view)

        val contentTextView: TextView = itemView.findViewById(R.id.note_content_text_view)

        val timestampTextView: TextView = itemView.findViewById(R.id.note_timestamp_text_view)

        val thumbnailImageView: android.widget.ImageView = itemView.findViewById(R.id.note_thumbnail_image_view)

        val tagsTextView: TextView = itemView.findViewById(R.id.note_tags_item_text_view)
    }
}