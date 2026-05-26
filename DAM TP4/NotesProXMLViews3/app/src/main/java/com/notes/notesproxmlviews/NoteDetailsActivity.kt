package com.notes.notesproxmlviews

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp.Companion.now
import com.google.firebase.firestore.DocumentReference
import android.util.Log

class NoteDetailsActivity : AppCompatActivity() {
    var titleEditText: EditText? = null
    var contentEditText: EditText? = null
    var saveNoteBtn: ImageButton? = null
    var pageTitleTextView: TextView? = null
    var title: String? = null

    var content: String? = null
    var docId: String? = null
    var isEditMode: Boolean = false
    var deleteNoteTextViewBtn: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)

        titleEditText = findViewById(R.id.notes_title_text)
        contentEditText = findViewById(R.id.note_content_text)
        saveNoteBtn = findViewById(R.id.save_note_btn)
        pageTitleTextView = findViewById(R.id.page_title)
        deleteNoteTextViewBtn = findViewById(R.id.delete_note_text_view_btn)

        //receive data
        title = intent.getStringExtra("title")
        content = intent.getStringExtra("content")
        docId = intent.getStringExtra("docId")

        if (docId != null && docId!!.isNotEmpty()) {
            isEditMode = true
        }

        titleEditText?.setText(title)
        contentEditText?.setText(content)

        if (isEditMode) {
            pageTitleTextView?.text = getString(R.string.edit_your_note)
            deleteNoteTextViewBtn?.visibility = View.VISIBLE
        }

        // debug
        if (saveNoteBtn == null) Log.e("NoteDetailsActivity", "saveNoteBtn ----> null")
        if (deleteNoteTextViewBtn == null) Log.e(
            "NoteDetailsActivity", "deleteNoteTextViewBtn ---->   null"
        )

        saveNoteBtn?.setOnClickListener {
            Log.d("NoteDetailsActivity", "Save clicado ---->---->---->---->")
            saveNote()
        }

        deleteNoteTextViewBtn?.setOnClickListener {
            Log.d("NoteDetailsActivity", "Delete btn clicado ---->---->---->---->---->")
            deleteNoteFromFirebase()
        }
    }

    private fun saveNote() {
        val noteTitle = titleEditText?.text.toString().trim()
        val noteContent = contentEditText?.text.toString().trim()

        if (noteTitle.isEmpty()) {
            titleEditText?.error = "Title is required"
            return
        }

        val note = Note()
        note.setTitle(noteTitle)
        note.setContent(noteContent)
        note.setTimestamp(now())

        saveNoteBtn?.isEnabled = false
        saveNoteToFirebase(note)
    }

    private fun saveNoteToFirebase(note: Note) {
        val documentReference: DocumentReference
        if (isEditMode) {
            //update the note
            documentReference = Utility.getCollectionReferenceForNotes().document(docId.toString())
        } else {
            //create new note
            documentReference = Utility.getCollectionReferenceForNotes().document()
        }

        documentReference.set(note).addOnCompleteListener(object : OnCompleteListener<Void?> {
            override fun onComplete(task: Task<Void?>) {
                if (task.isSuccessful) {
                    Utility.showToast(this@NoteDetailsActivity, "Note saved successfully")
                    Log.d("NoteDetailsActivity", "finish chamado ---->---->---->---->")
                    finish() // Sai da nota
                } else {
                    Utility.showToast(this@NoteDetailsActivity, "Failed while saving note")
                    saveNoteBtn?.isEnabled = true
                }
            }
        })
    }

    fun deleteNoteFromFirebase() {

        deleteNoteTextViewBtn?.isEnabled = false // Previne cliques múltiplos
        val documentReference: DocumentReference = Utility.getCollectionReferenceForNotes().document(
            docId.toString()
        )
        documentReference.delete().addOnCompleteListener(object : OnCompleteListener<Void?> {
            override fun onComplete(task: Task<Void?>) {
                if (task.isSuccessful) {
                    Utility.showToast(this@NoteDetailsActivity, "Note deleted successfully")
                    finish() // Sai da nota
                } else {
                    Utility.showToast(this@NoteDetailsActivity, "Failed while deleting note")
                    deleteNoteTextViewBtn?.isEnabled = true
                    finish()
                }
            }
        })
    }
}