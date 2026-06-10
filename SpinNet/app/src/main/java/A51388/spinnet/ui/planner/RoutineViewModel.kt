package A51388.spinnet.ui.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import A51388.spinnet.data.model.Routine
import A51388.spinnet.data.model.Shot
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RoutineViewModel : ViewModel() {

    private val db  = FirebaseFirestore.getInstance()
    //guarda o uid do user autenticado, se n existir guarda uma string vazia
    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Lista de rotinas guardadas — a UI observa isto
    private val _routines = MutableStateFlow<List<Routine>>(emptyList())
    val routines: StateFlow<List<Routine>> = _routines

    init {
        loadRoutines()
    }

    private fun loadRoutines() {
        val currentUid = uid
        if (currentUid.isEmpty()) return
        // Escuta mudanças em tempo real no Firestore
        db.collection("users").document(currentUid)
            .collection("routines")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                _routines.value = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Routine::class.java)?.copy(id = doc.id)
                }
            }
    }

    fun saveRoutine(title: String, shots: List<Shot>) {
        val currentUid = uid
        if (currentUid.isEmpty()) return
        viewModelScope.launch {
            val routine = mapOf(
                "title"     to title,
                "shots"     to shots,
                "createdAt" to System.currentTimeMillis()
            )
            db.collection("users").document(currentUid)
                .collection("routines")
                .add(routine)
                .await()
        }
    }

    fun deleteRoutine(id: String) {
        val currentUid = uid
        if (currentUid.isEmpty()) return
        viewModelScope.launch {
            db.collection("users").document(currentUid)
                .collection("routines")
                .document(id)
                .delete()
                .await()
        }
    }
}