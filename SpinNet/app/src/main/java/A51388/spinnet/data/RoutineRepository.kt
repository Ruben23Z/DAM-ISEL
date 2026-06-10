package A51388.spinnet.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RoutineRepository {


    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()


    private fun routinesRef() = db
        .collection("users")
        .document(auth.currentUser!!.uid)
        .collection("routines")




}