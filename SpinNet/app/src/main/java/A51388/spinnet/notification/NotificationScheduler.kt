package A51388.spinnet.notification

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    fun schedule(
        context: Context, routineId: String, title: String, shots: Int, scheduledAt: Long
    ) {
        val delay = scheduledAt - System.currentTimeMillis()
        if (delay <= 0) return

        val data = workDataOf(
            "routineId" to routineId, "title" to title, "shots" to shots
        )

        val request = OneTimeWorkRequestBuilder<NotificationWorker>().setInitialDelay(
            delay, TimeUnit.MILLISECONDS
        ).setInputData(data).addTag(routineId).build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(routineId, ExistingWorkPolicy.REPLACE, request)
    }

    fun cancel(context: Context, routineId: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(routineId)
    }
}