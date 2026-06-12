package A51388.spinnet.ui.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import A51388.spinnet.data.model.Routine
import A51388.spinnet.data.model.Shot
import A51388.spinnet.data.model.TrainingPlan
import A51388.spinnet.data.model.PlanRoutineItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RoutineViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _routines = MutableStateFlow<List<Routine>>(emptyList())
    val routines: StateFlow<List<Routine>> = _routines

    private val _trainingPlans = MutableStateFlow<List<TrainingPlan>>(emptyList())
    val trainingPlans: StateFlow<List<TrainingPlan>> = _trainingPlans

    private val _cloneSuccess = MutableStateFlow<String?>(null)
    val cloneSuccess: StateFlow<String?> = _cloneSuccess

    val activeTrainingRoutine = MutableStateFlow<Routine?>(null)
    val activeTrainingPlan = MutableStateFlow<TrainingPlan?>(null)

    init {
        loadRoutines()
        loadTrainingPlans()
    }

    private fun loadRoutines() {
        val currentUid = uid
        if (currentUid.isEmpty()) return
        db.collection("users").document(currentUid).collection("routines")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                _routines.value = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Routine::class.java)?.copy(id = doc.id)
                }
            }
    }

    private fun loadTrainingPlans() {
        val currentUid = uid
        if (currentUid.isEmpty()) return
        db.collection("users").document(currentUid).collection("trainingPlans")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                _trainingPlans.value = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(TrainingPlan::class.java)?.copy(id = doc.id)
                }
            }
    }

    fun saveRoutine(title: String, shots: List<Shot>) {
        val currentUid = uid
        if (currentUid.isEmpty()) return
        viewModelScope.launch {
            val routine = mapOf(
                "title" to title, "shots" to shots, "createdAt" to System.currentTimeMillis()
            )
            db.collection("users").document(currentUid).collection("routines").add(routine).await()
        }
    }

    fun deleteRoutine(id: String) {
        val currentUid = uid
        if (currentUid.isEmpty()) return
        viewModelScope.launch {
            db.collection("users").document(currentUid).collection("routines").document(id).delete()
                .await()
        }
    }

    fun addFromCommunity(routine: Routine) {
        if (uid.isEmpty()) return
        viewModelScope.launch {
            try {
                db.collection("users").document(uid).collection("routines").add(
                    mapOf(
                        "title" to "${routine.title} (copy)",
                        "shots" to routine.shots,
                        "createdAt" to System.currentTimeMillis()
                    )
                ).await()
                _cloneSuccess.value = "${routine.title} adicionado ao teu plano!"
            } catch (e: Exception) {
                _cloneSuccess.value = "Erro: ${e.localizedMessage}"
                android.util.Log.e("RoutineViewModel", "Erro ao adicionar da comunidade", e)
            }
        }
    }

    fun shareRoutine(routine: Routine, customTitle: String, description: String) {
        if (uid.isEmpty()) return
        viewModelScope.launch {
            try {
                val data = mapOf(
                    "routineId" to routine.id,
                    "title" to customTitle,
                    "description" to description,
                    "shots" to routine.shots,
                    "createdAt" to System.currentTimeMillis(),
                    "sharedWith" to listOf(uid),
                    "sharedBy" to uid,
                    "isPublic" to true
                )
                db.collection("sharedRoutines").add(data).await()
                _cloneSuccess.value = "$customTitle partilhada com a comunidade!"
            } catch (e: Exception) {
                _cloneSuccess.value = "Erro: ${e.localizedMessage}"
                android.util.Log.e("RoutineViewModel", "Erro ao partilhar rotina", e)
            }
        }
    }

    fun saveTrainingPlan(id: String? = null, title: String, description: String, items: List<PlanRoutineItem>, workTimeSeconds: Int, restTimeSeconds: Int) {
        val currentUid = uid
        if (currentUid.isEmpty()) return
        viewModelScope.launch {
            val plan = mapOf(
                "title" to title,
                "description" to description,
                "routines" to items,
                "createdAt" to System.currentTimeMillis(),
                "uid" to currentUid,
                "workTimeSeconds" to workTimeSeconds,
                "restTimeSeconds" to restTimeSeconds
            )
            if (id != null && id.isNotBlank()) {
                db.collection("users").document(currentUid).collection("trainingPlans").document(id).set(plan).await()
            } else {
                db.collection("users").document(currentUid).collection("trainingPlans").add(plan).await()
            }
        }
    }

    fun deleteTrainingPlan(id: String) {
        val currentUid = uid
        if (currentUid.isEmpty()) return
        viewModelScope.launch {
            db.collection("users").document(currentUid).collection("trainingPlans").document(id).delete()
                .await()
        }
    }

    fun shareTrainingPlan(plan: TrainingPlan, customTitle: String, description: String) {
        if (uid.isEmpty()) return
        viewModelScope.launch {
            try {
                val data = mapOf(
                    "planId" to plan.id,
                    "title" to customTitle,
                    "description" to description,
                    "routines" to plan.routines,
                    "createdAt" to System.currentTimeMillis(),
                    "sharedBy" to uid,
                    "isPublic" to true
                )
                db.collection("sharedTrainingPlans").add(data).await()
                _cloneSuccess.value = "$customTitle partilhado com a comunidade!"
            } catch (e: Exception) {
                _cloneSuccess.value = "Erro: ${e.localizedMessage}"
            }
        }
    }

    fun addPlanFromCommunity(title: String, description: String, routines: List<PlanRoutineItem>) {
        if (uid.isEmpty()) return
        viewModelScope.launch {
            try {
                db.collection("users").document(uid).collection("trainingPlans").add(
                    mapOf(
                        "title" to "$title (copy)",
                        "description" to description,
                        "routines" to routines,
                        "createdAt" to System.currentTimeMillis(),
                        "uid" to uid
                    )
                ).await()
                _cloneSuccess.value = "$title adicionado aos teus planos!"
            } catch (e: Exception) {
                _cloneSuccess.value = "Erro: ${e.localizedMessage}"
            }
        }
    }

    fun clearCloneMessage() {
        _cloneSuccess.value = null
    }

    fun completeSession(
        routineId: String,
        routineTitle: String,
        durationMinutes: Int,
        reps: Int,
        accuracy: Int,
        racketSide: String,
        notes: String = ""
    ) {
        if (uid.isEmpty()) return
        viewModelScope.launch {
            try {
                val sessionMap = mapOf(
                    "uid" to uid,
                    "routineId" to routineId,
                    "routineTitle" to routineTitle,
                    "durationMinutes" to durationMinutes,
                    "reps" to reps,
                    "accuracy" to accuracy,
                    "completedAt" to System.currentTimeMillis(),
                    "racketSide" to racketSide,
                    "notes" to notes
                )
                db.collection("users")
                    .document(uid)
                    .collection("sessions")
                    .add(sessionMap).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}