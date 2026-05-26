package com.notes.notesproxmlviews

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.View
import androidx.appcompat.app.AlertDialog

import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp.Companion.now
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import org.json.JSONArray

class NoteDetailsActivity : AppCompatActivity() {
    var titleEditText: EditText? = null
    var contentEditText: EditText? = null
    var saveNoteBtn: ImageButton? = null
    var attachImageBtn: ImageButton? = null
    var noteImageView: ImageView? = null
    var resizeImageBtn: ImageButton? = null
    var deleteImageBtn: ImageButton? = null
    var noteTagsTextView: TextView? = null
    var pageTitleTextView: TextView? = null
    var title: String? = null

    var content: String? = null
    var docId: String? = null
    var isEditMode: Boolean = false
    var deleteNoteTextViewBtn: TextView? = null

    // New features UI
    var noteContentContainer: LinearLayout? = null
    var wordCounterTextView: TextView? = null
    var colorPickerContainer: LinearLayout? = null
    var manualTagsContainer: LinearLayout? = null
    var noteImageContainer: FrameLayout? = null
    var addTagEditText: EditText? = null
    var addTagBtn: android.widget.Button? = null

    var currentImageBase64: String? = null
    var currentTags: MutableList<String> = mutableListOf()
    var currentNoteColor: String = "#FFFFFF" // Default white

    private val availableColors = listOf(
        "#FFFFFF", // White
        "#FFF9C4", // Pastel Yellow
        "#FFCDD2", // Pastel Red/Pink
        "#C8E6C9", // Pastel Green
        "#B3E5FC", // Pastel Blue
        "#E1BEE7"  // Pastel Purple
    )

    private val presetTags = listOf("Trabalho", "Estudos", "Pessoal", "Finanças")

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                
                // Scale down bitmap to save Firestore space
                val scaledBitmap = scaleBitmapDown(bitmap, 800)
                
                val outputStream = ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
                val byteArray = outputStream.toByteArray()
                currentImageBase64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)
                
                noteImageView?.setImageBitmap(scaledBitmap)
                noteImageContainer?.visibility = View.VISIBLE
                noteImageView?.visibility = View.VISIBLE
                resizeImageBtn?.visibility = View.VISIBLE
                deleteImageBtn?.visibility = View.VISIBLE
                
            } catch (e: Exception) {
                e.printStackTrace()
                Utility.showToast(this, "Failed to load image")
            }
        }
    }
    private fun showImageOptions() {
        val options = arrayOf("Redimensionar", "Apagar", "Cancelar")
        AlertDialog.Builder(this)
            .setTitle("Opções da imagem")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> { // Resize
                        currentImageBase64?.let { base64 ->
                            val decoded = Base64.decode(base64, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
                            val resized = scaleBitmapDown(bitmap, 400)
                            val stream = ByteArrayOutputStream()
                            resized.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                            currentImageBase64 = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
                            noteImageView?.setImageBitmap(resized)
                        }
                    }
                    1 -> { // Delete
                        currentImageBase64 = null
                        noteImageView?.setImageBitmap(null)
                        noteImageContainer?.visibility = View.GONE
                        noteImageView?.visibility = View.GONE
                    }
                    else -> dialog.dismiss()
                }
            }
            .show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)

        titleEditText = findViewById(R.id.notes_title_text)
        contentEditText = findViewById(R.id.note_content_text)
        saveNoteBtn = findViewById(R.id.save_note_btn)
        attachImageBtn = findViewById(R.id.attach_image_btn)
        noteImageView = findViewById(R.id.note_image_view)
        resizeImageBtn = findViewById(R.id.btn_resize_image)
        deleteImageBtn = findViewById(R.id.btn_delete_image)
        // Initially hide overlay buttons
        resizeImageBtn?.visibility = View.GONE
        deleteImageBtn?.visibility = View.GONE
        noteTagsTextView = findViewById(R.id.note_tags_text_view)
        pageTitleTextView = findViewById(R.id.page_title)
        deleteNoteTextViewBtn = findViewById(R.id.delete_note_text_view_btn)

        // Bind new feature views
        noteContentContainer = findViewById(R.id.note_content_container)
        wordCounterTextView = findViewById(R.id.word_counter_text_view)
        colorPickerContainer = findViewById(R.id.color_picker_container)
        manualTagsContainer = findViewById(R.id.manual_tags_container)
        noteImageContainer = findViewById(R.id.note_image_container)
        addTagEditText = findViewById(R.id.add_tag_edit_text)
        addTagBtn = findViewById(R.id.add_tag_btn)

        // receive data
        title = intent.getStringExtra("title")
        content = intent.getStringExtra("content")
        currentImageBase64 = intent.getStringExtra("imageBase64")
        currentNoteColor = intent.getStringExtra("noteColor") ?: "#FFFFFF"
        val receivedTags = intent.getStringArrayListExtra("tags")
        if (receivedTags != null) {
            currentTags = receivedTags.toMutableList()
        }
        docId = intent.getStringExtra("docId")

        if (docId != null && docId!!.isNotEmpty()) {
            isEditMode = true
        }

        titleEditText?.setText(title)
        contentEditText?.setText(content)
        
        // Load background color
        applyNoteColor(currentNoteColor)
        
        if (currentImageBase64 != null && currentImageBase64!!.isNotEmpty()) {
            try {
                val decodedString = Base64.decode(currentImageBase64, Base64.DEFAULT)
                val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                noteImageView?.setImageBitmap(decodedByte)
                noteImageContainer?.visibility = View.VISIBLE
                noteImageView?.visibility = View.VISIBLE
                // Show overlay icons when image is present
                resizeImageBtn?.visibility = View.VISIBLE
                deleteImageBtn?.visibility = View.VISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        updateTagsDisplay()

        if (isEditMode) {
            pageTitleTextView?.text = getString(R.string.edit_your_note)
            deleteNoteTextViewBtn?.visibility = View.VISIBLE
        }

        // Action listeners
        attachImageBtn?.setOnClickListener {
            // Launch image picker
            pickImage.launch("image/*")
        }

        resizeImageBtn?.setOnClickListener {
            // Resize image
            currentImageBase64?.let { base64 ->
                val decoded = Base64.decode(base64, Base64.NO_WRAP)
                val bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
                val resized = scaleBitmapDown(bitmap, 400)
                val stream = ByteArrayOutputStream()
                resized.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                currentImageBase64 = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
                noteImageView?.setImageBitmap(resized)
            }
        }

        deleteImageBtn?.setOnClickListener {
            // Delete image
            currentImageBase64 = null
            noteImageView?.setImageBitmap(null)
            noteImageContainer?.visibility = View.GONE
            noteImageView?.visibility = View.GONE
            resizeImageBtn?.visibility = View.GONE
            deleteImageBtn?.visibility = View.GONE
        }

        saveNoteBtn?.setOnClickListener {
            saveNote()
        }

        deleteNoteTextViewBtn?.setOnClickListener {
            deleteNoteFromFirebase()
        }

        addTagBtn?.setOnClickListener {
            val rawTag = addTagEditText?.text?.toString()?.trim() ?: ""
            if (rawTag.isNotEmpty()) {
                val tagClean = rawTag.replace(Regex("^#+"), "").trim()
                if (tagClean.isNotEmpty()) {
                    if (!currentTags.contains(tagClean)) {
                        currentTags.add(tagClean)
                    }
                    addTagEditText?.setText("")
                    setupTagPicker()
                    updateTagsDisplay()
                }
            }
        }

        // Live word counter setup
        contentEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s?.toString() ?: ""
                val charCount = text.length
                val words = text.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.size
                wordCounterTextView?.text = "$words palavras | $charCount caract."
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Initialize dynamic widgets
        setupColorPicker()
        setupTagPicker()
        
        // Trigger word counter initial run
        contentEditText?.text?.let {
            val charCount = it.length
            val words = it.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.size
            wordCounterTextView?.text = "$words palavras | $charCount caract."
        }
    }

    // Scale bitmap down to a maximum dimension while preserving aspect ratio
    private fun scaleBitmapDown(original: Bitmap, maxDimension: Int): Bitmap {
        val width = original.width
        val height = original.height
        val ratio = if (width >= height) {
            maxDimension.toFloat() / width
        } else {
            maxDimension.toFloat() / height
        }
        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()
        return Bitmap.createScaledBitmap(original, newWidth, newHeight, true)
    }

    private fun setupColorPicker() {
        colorPickerContainer?.removeAllViews()
        for (colorHex in availableColors) {
            val view = View(this)
            val size = (36 * resources.displayMetrics.density).toInt()
            val margin = (8 * resources.displayMetrics.density).toInt()
            val params = LinearLayout.LayoutParams(size, size)
            params.setMargins(margin, margin, margin, margin)
            view.layoutParams = params

            // Draw circle shape
            val drawable = GradientDrawable()
            drawable.shape = GradientDrawable.OVAL
            drawable.setColor(Color.parseColor(colorHex))
            drawable.setStroke(3, Color.parseColor("#CCCCCC"))
            view.background = drawable

            view.setOnClickListener {
                currentNoteColor = colorHex
                applyNoteColor(colorHex)
            }
            colorPickerContainer?.addView(view)
        }
    }

    private fun applyNoteColor(colorHex: String) {
        try {
            noteContentContainer?.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor(colorHex))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupTagPicker() {
        manualTagsContainer?.removeAllViews()
        val allTags = (presetTags + currentTags).distinct()
        for (tag in allTags) {
            val textView = TextView(this)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val margin = (4 * resources.displayMetrics.density).toInt()
            params.setMargins(margin, margin, margin, margin)
            textView.layoutParams = params
            textView.setPadding(24, 12, 24, 12)
            textView.text = tag
            textView.textSize = 14f
            
            // Set style based on selection
            updateSingleTagStyle(textView, tag)

            textView.setOnClickListener {
                if (currentTags.contains(tag)) {
                    currentTags.remove(tag)
                } else {
                    currentTags.add(tag)
                }
                setupTagPicker()
                updateTagsDisplay()
            }
            manualTagsContainer?.addView(textView)
        }
    }

    private fun updateSingleTagStyle(textView: TextView, tag: String) {
        val drawable = GradientDrawable()
        drawable.cornerRadius = 30f
        if (currentTags.contains(tag)) {
            drawable.setColor(Color.parseColor("#005B94"))
            textView.setTextColor(Color.WHITE)
        } else {
            drawable.setColor(Color.parseColor("#E0E0E0"))
            textView.setTextColor(Color.BLACK)
        }
        textView.background = drawable
    }

    private fun updateTagsDisplay() {
        if (currentTags.isNotEmpty()) {
            noteTagsTextView?.text = currentTags.joinToString(" ") { "#$it" }
            noteTagsTextView?.visibility = View.VISIBLE
        } else {
            noteTagsTextView?.visibility = View.GONE
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
        note.setImageBase64(currentImageBase64)
        note.setTags(currentTags)
        note.setNoteColor(currentNoteColor)

        saveNoteBtn?.isEnabled = false
        saveNoteToFirebase(note)
    }

    private fun saveNoteToFirebase(note: Note) {
        val documentReference: DocumentReference
        if (isEditMode) {
            documentReference = Utility.getCollectionReferenceForNotes().document(docId.toString())
        } else {
            documentReference = Utility.getCollectionReferenceForNotes().document()
        }

        documentReference.set(note).addOnCompleteListener(object : OnCompleteListener<Void?> {
            override fun onComplete(task: Task<Void?>) {
                if (task.isSuccessful) {
                    Utility.showToast(this@NoteDetailsActivity, "Note saved successfully")
                    finish()
                } else {
                    Utility.showToast(this@NoteDetailsActivity, "Failed while saving note")
                    saveNoteBtn?.isEnabled = true
                }
            }
        })
    }

    fun deleteNoteFromFirebase() {
        deleteNoteTextViewBtn?.isEnabled = false
        val documentReference: DocumentReference = Utility.getCollectionReferenceForNotes().document(docId.toString())
        documentReference.delete().addOnCompleteListener(object : OnCompleteListener<Void?> {
            override fun onComplete(task: Task<Void?>) {
                if (task.isSuccessful) {
                    Utility.showToast(this@NoteDetailsActivity, "Note deleted successfully")
                    finish()
                } else {
                    Utility.showToast(this@NoteDetailsActivity, "Failed while deleting note")
                    deleteNoteTextViewBtn?.isEnabled = true
                    finish()
                }
            }
        })
    }
}