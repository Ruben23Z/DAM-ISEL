package A51388.spinnet.ui.profile

import androidx.lifecycle.ViewModel
import A51388.spinnet.data.model.TrainingSession
import android.icu.util.Calendar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class PerformanceStats(
    val volumeGrowthPercentage: Int = 0,
    val totalDrills: Int = 0,
    val dayStreak: Int = 0,

    val timeTrained: Long = 0L,
    val averageSessionDurationMinutes: Int = 0,
    val longestSessionMinutes: Int = 0,
    val activeDaysThisMonth: Int = 0,
    val bestDayStreak: Int = 0,
    val racketPreferredSide: String = "",
    val daysSinceLastSession: Int = 0,

    val recentSessions: List<TrainingSession> = emptyList(),
    val timeTrainedThisWeek: Long = 0L,
)

class PerformanceViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val uid = auth.currentUser?.uid ?: ""


    //permite escrever dados novos, se algo muda no firestore o _stats recebe e atualiza-se
    private val _stats = MutableStateFlow(PerformanceStats())

    //permite ler dados do _stats, recebe os valores de _stats, e mostra a UI, esta não pode alterar os valores de stats
    val stats: StateFlow<PerformanceStats> = _stats


    init {
        loadSession()
    }


    fun loadSession() {
        if (uid.isEmpty()) return


        db.collection("users").document(uid).collection("sessions")
            .orderBy("completedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener


                val sessions = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(TrainingSession::class.java)?.copy(id = doc.id)

                }
                processStats(sessions)

            }
    }


    fun processStats(sessions: List<TrainingSession>) {
        if (sessions.isEmpty()) {
            _stats.value = PerformanceStats()
            return
        }

        val now = System.currentTimeMillis()
        val dayMillis = 86_400_000L
        val oneWeekMs = 7 * dayMillis

        val totalDrills = sessions.size
        val timeTrained = sessions.sumOf { it.durationMinutes * 60L * 1000L }

        val averageSessionDurationMinutes = (timeTrained / totalDrills / 60_000L).toInt()
        val longestSessionMinutes = (sessions.maxOfOrNull { it.durationMinutes }) ?: 0


        val daysSinceLastSession = ((now - sessions.first().completedAt) / dayMillis).toInt()

        val racketPreferredSide =
            sessions.groupBy { it.racketSide }.maxByOrNull { it.value.size }?.key ?: "N/A"

        val timeTrainedThisWeek: Long = sessions.filter { it.completedAt >= (now - oneWeekMs) }
            .sumOf { it.durationMinutes.toLong() * 60L * 1000L }

        val timeTrainedLastWeek: Long =
            sessions.filter { it.completedAt in (now - 2 * oneWeekMs)..<(now - oneWeekMs) }
                .sumOf { it.durationMinutes.toLong() * 60L * 1000L }

        var volumeGrowthPercentage = 0

        if (timeTrainedLastWeek == 0L) {
            if (timeTrainedThisWeek > 0L) {
                volumeGrowthPercentage = 100
            } else {
                volumeGrowthPercentage = 0
            }
        } else {
            volumeGrowthPercentage =
                (((timeTrainedThisWeek - timeTrainedLastWeek).toDouble() / timeTrainedLastWeek) * 100).toInt()
        }

        val currentCalendar = Calendar.getInstance()
        val currentMonth = currentCalendar.get(Calendar.MONTH)
        val currentYear = currentCalendar.get(Calendar.YEAR)

        val activeDaysThisMonth = sessions.mapNotNull { doc ->
            val cal = Calendar.getInstance().apply { timeInMillis = doc.completedAt }
            if (cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear) {
                cal.get(Calendar.DAY_OF_MONTH)
            } else null
        }.toSet().size



        _stats.value = PerformanceStats(
            totalDrills = totalDrills,
            timeTrained = timeTrained,
            averageSessionDurationMinutes = averageSessionDurationMinutes,
            dayStreak = calculateStreak(sessions),
            bestDayStreak = calculateBestStreak(sessions),
            activeDaysThisMonth = activeDaysThisMonth,
            timeTrainedThisWeek = timeTrainedThisWeek,
            racketPreferredSide = racketPreferredSide,
            recentSessions = sessions.take(10),
            volumeGrowthPercentage = volumeGrowthPercentage,
            longestSessionMinutes = longestSessionMinutes,
            daysSinceLastSession = daysSinceLastSession
        )

    }

    private fun calculateStreak(sessions: List<TrainingSession>): Int {
        val dayMs = 86_400_000L
        val today = System.currentTimeMillis() / dayMs

        val sessionDays = sessions.map { it.completedAt / dayMs }.toSortedSet(reverseOrder())

        var streak = 0
        var expected = today
        for (day in sessionDays) {
            if (day == expected || day == expected - 1) {
                streak++
                expected = day - 1
            } else {
                break
            }
        }
        return streak
    }

    private fun calculateBestStreak(sessions: List<TrainingSession>): Int {
        val dayMs = 86_400_000L
        val sessionDays = sessions.map { it.completedAt / dayMs }.toSortedSet()

        var currentStreak = 1
        var bestStreak = 1
        var previousDay = sessionDays.firstOrNull() ?: return 0

        for (day in sessionDays.drop(1)) {
            if (day == previousDay + 1) {
                currentStreak++
            } else if (day > previousDay + 1) {
                currentStreak = 1 // Falhou um dia reseta
            }

            if (currentStreak > bestStreak) {
                bestStreak = currentStreak
            }
            previousDay = day
        }
        return bestStreak
    }
}