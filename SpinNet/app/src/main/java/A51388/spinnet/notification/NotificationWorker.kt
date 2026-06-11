package A51388.spinnet.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import A51388.spinnet.MainActivity

class NotificationWorker(
    private val context: Context, params: WorkerParameters
) : Worker(context, params) {


    companion object {
        const val CHANNEL_ID = "spinnet_training"
    }

    override fun doWork(): Result {
        val title = inputData.getString("title") ?: "Hora do treino!!"
        val shots = inputData.getInt("shots", 0)
        val routineID = inputData.getString("routineId") ?: ""

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("routineId", routineID)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID, "Treinos", NotificationManager.IMPORTANCE_HIGH
        )
        manager.createNotificationChannel(channel)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle(title)
            .setContentText("Você tem $shots shots para treinar hoje! No Pain No Gain!")
            .setContentIntent(pendingIntent).setAutoCancel(true).build()

        manager.notify(routineID.hashCode(), notification)
        return Result.success()
    }


}