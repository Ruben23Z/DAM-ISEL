package com.notes.notesproxmlviews

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {
    var addNoteBtn: FloatingActionButton? = null
    var recyclerView: RecyclerView? = null
    var menuBtn: ImageButton? = null
    var noteAdapter: NoteAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        addNoteBtn = findViewById<FloatingActionButton?>(R.id.add_note_btn)
        recyclerView = findViewById<RecyclerView?>(R.id.recycler_view)
        menuBtn = findViewById<ImageButton?>(R.id.menu_btn)

        addNoteBtn!!.setOnClickListener(View.OnClickListener { v: View? ->
            startActivity(
                Intent(
                    this@MainActivity, NoteDetailsActivity::class.java
                )
            )
        })
        menuBtn!!.setOnClickListener(View.OnClickListener { v: View? -> showMenu() })
        setupRecyclerView();
    }


    fun showMenu() {

        val popupMenu = android.widget.PopupMenu(this@MainActivity, menuBtn)
        popupMenu.menu.add("Logout")
        popupMenu.setOnMenuItemClickListener { menuItem ->
            if (menuItem.title == "Logout") {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
                true
            } else {
                false
            }
        }
        popupMenu.show()
    }

    fun setupRecyclerView() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.e("MainActivity", "nao signado, para o login")
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
            return
        }
        val query: Query = Utility.getCollectionReferenceForNotes()
            .orderBy("timestamp", Query.Direction.DESCENDING)
        val options: FirestoreRecyclerOptions<Note> =
            FirestoreRecyclerOptions.Builder<Note>().setQuery(query, Note::class.java).build()

        recyclerView?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        noteAdapter = NoteAdapter(options, this)
        recyclerView?.adapter = noteAdapter
    }

    override fun onStart() {
        super.onStart()
        noteAdapter?.startListening()
    }

    override fun onResume() {
        super.onResume()
        noteAdapter?.notifyDataSetChanged()
    }

    override fun onStop() {
        super.onStop()
        noteAdapter?.stopListening()
    }
}
