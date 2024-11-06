package nz.ac.canterbury.seng303.aboutthejourney.helpers.workers

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import nz.ac.canterbury.seng303.aboutthejourney.MainActivity
import nz.ac.canterbury.seng303.aboutthejourney.R

/**
 * A worker that shows a notification for a journey.
 * @param context The context to show the notification in.
 * @param workerParams The parameters for the worker.
 */
class JourneyNotificationWorker(
    val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    /**
     * Does the work for showing a notification for a journey.
     * @return The result of the work.
     */
    override fun doWork(): Result {
        val journeyName = inputData.getString("journeyName") ?: return Result.failure()
        val journeyId = inputData.getInt("journeyId", -1)

        if (journeyId == -1) {
            return Result.failure()
        }

        showNotification(journeyName, journeyId)
        return Result.success()
    }

    /**
     * Shows a notification for the journey.
     * @param journeyName The name of the journey.
     * @param journeyId The ID of the journey.
     */
    private fun showNotification(journeyName: String, journeyId: Int) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(applicationContext, MainActivity.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.another_day_on_your_journey, journeyName))
            .setContentText(context.getString(R.string.click_below_to_add_a_point_of_interest))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(journeyId, builder.build())
        }
    }

}