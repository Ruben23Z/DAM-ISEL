package A51388.spinnet.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import A51388.spinnet.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PlayerProfileViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val uid = auth.currentUser?.uid ?: ""

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile

    private val _saveState = MutableStateFlow<String?>(null)
    val saveState: StateFlow<String?> = _saveState

    private val _uploadingPhoto = MutableStateFlow(false)
    val uploadingPhoto: StateFlow<Boolean> = _uploadingPhoto

    init {
        loadProfile()
    }

    fun loadProfile() {
        if (uid.isEmpty()) return
        db.collection("users").document(uid).addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener
            _profile.value = snapshot.toObject(UserProfile::class.java)
                ?: UserProfile(uid = uid, email = auth.currentUser?.email ?: "")
        }
    }

    fun updateProfile(updated: UserProfile) {
        if (uid.isEmpty()) return
        viewModelScope.launch {
            try {
                db.collection("users").document(uid).set(updated).await()
                _saveState.value = "Perfil guardado."
            } catch (e: Exception) {
                android.util.Log.e("PlayerProfileVM", "Erro ao guardar perfil", e)
                _saveState.value = "Erro: ${e.localizedMessage}"
            }
        }
    }

    fun uploadProfilePhoto(uri: Uri) {
        if (uid.isEmpty()) return
        _uploadingPhoto.value = true
        viewModelScope.launch {
            try {
                val ref = storage.reference.child("profileImages/$uid.jpg")
                ref.putFile(uri).await()
                val downloadUrl = ref.downloadUrl.await().toString()
                val current = _profile.value ?: UserProfile(uid = uid)
                db.collection("users").document(uid)
                    .set(current.copy(profileImage = downloadUrl)).await()
                _saveState.value = "Foto actualizada."
            } catch (e: Exception) {
                android.util.Log.e("PlayerProfileVM", "Erro ao fazer upload da foto", e)
                _saveState.value = "Erro: ${e.localizedMessage}"
            } finally {
                _uploadingPhoto.value = false
            }
        }
    }

    fun clearSaveState() { _saveState.value = null }
}