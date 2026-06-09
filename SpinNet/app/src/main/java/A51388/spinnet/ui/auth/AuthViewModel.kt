package A51388.spinnet.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _state.value = AuthState.Success
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.message ?: "Erro ao iniciar sessão")
            }
        }
    }

    fun register(email: String, password: String, username: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                result.user?.uid?.let { uid ->
                    db.collection("users").document(uid).set(
                        mapOf(
                            "uid" to uid,
                            "username" to username,
                            "email" to email,
                            "profileImage" to ""
                        )
                    ).await()
                }
                _state.value = AuthState.Success
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.message ?: "Erro ao criar conta")
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                result.user?.let { user ->
                    val doc = db.collection("users").document(user.uid).get().await()
                    if (!doc.exists()) {
                        db.collection("users").document(user.uid).set(
                            mapOf(
                                "uid" to user.uid,
                                "username" to (user.displayName ?: ""),
                                "email" to (user.email ?: ""),
                                "profileImage" to (user.photoUrl?.toString() ?: "")
                            )
                        ).await()
                    }
                }
                _state.value = AuthState.Success
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.message ?: "Erro com Google Sign-In")
            }
        }
    }

    fun signInWithFacebook(idToken: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val credential = FacebookAuthProvider.getCredential(idToken)
                val result = auth.signInWithCredential(credential).await()
                result.user?.let { user ->
                    val doc = db.collection("users").document(user.uid).get().await()
                    if (!doc.exists()) {
                        db.collection("users").document(user.uid).set(
                            mapOf(
                                "uid" to user.uid,
                                "username" to (user.displayName ?: ""),
                                "email" to (user.email ?: ""),
                                "profileImage" to (user.photoUrl?.toString() ?: "")
                            )
                        ).await()
                    }
                }
                _state.value = AuthState.Success
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.message ?: "Erro com Facebook Sign-In")
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _state.value = AuthState.Idle
    }

    fun resetState() {
        _state.value = AuthState.Idle
    }
}
