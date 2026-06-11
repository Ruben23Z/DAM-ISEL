package A51388.spinnet.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import A51388.spinnet.data.model.SharedRoutine
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CommunityViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _feed = MutableStateFlow<List<SharedRoutine>>(emptyList())
    val feed: StateFlow<List<SharedRoutine>> = _feed

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage

    val currentUid: String get() = uid

    init {
        loadFeed()
    }

    private fun loadFeed() {
        db.collection("sharedRoutines")
            .whereEqualTo("isPublic", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                _feed.value = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(SharedRoutine::class.java)?.copy(id = doc.id)
                }
            }
    }

    fun unshareRoutine(routineId: String) {
        viewModelScope.launch {
            try {
                db.collection("sharedRoutines").document(routineId).delete().await()
                _actionMessage.value = "Publicação eliminada."
            } catch (e: Exception) {
                android.util.Log.e("CommunityViewModel", "Erro ao eliminar", e)
                _actionMessage.value = "Erro: ${e.localizedMessage}"
            }
        }
    }

    fun updateSharedRoutine(routineId: String, newTitle: String, newDescription: String) {
        viewModelScope.launch {
            try {
                db.collection("sharedRoutines").document(routineId).update(
                    mapOf("title" to newTitle, "description" to newDescription)
                ).await()
                _actionMessage.value = "Publicação actualizada."
            } catch (e: Exception) {
                android.util.Log.e("CommunityViewModel", "Erro ao actualizar", e)
                _actionMessage.value = "Erro: ${e.localizedMessage}"
            }
        }
    }

    fun setRoutinePrivate(routineId: String) {
        viewModelScope.launch {
            try {
                db.collection("sharedRoutines").document(routineId)
                    .update("isPublic", false).await()
                _actionMessage.value = "Rotina tornada privada."
            } catch (e: Exception) {
                android.util.Log.e("CommunityViewModel", "Erro ao tornar privado", e)
                _actionMessage.value = "Erro: ${e.localizedMessage}"
            }
        }
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }
}