package nz.ac.canterbury.seng303.aboutthejourney.helpers.workers

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

/**
 * Schedules a journey notification worker to run daily for the given journey.
 * @param context The context to schedule the worker in.
 * @param journeyId The id of the journey.
 * @param journeyName The name of the journey.
 */
fun scheduleJourneyNotificationWorker(
    context: Context,
    journeyId : Int,
    journeyName: String,
) {
    val data = workDataOf(
        "journeyName" to journeyName,
        "journeyId" to journeyId
    )

    // NOTE: minimum notification repeat is 15 minutes
    // https://stackoverflow.com/questions/54025661/periodic-work-manager-not-showing-notification-on-android-pie-when-not-charging
    val dailyWorkRequest = PeriodicWorkRequestBuilder<JourneyNotificationWorker>(1, TimeUnit.DAYS)
        .setInputData(data)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "JourneyNotificationWorker_$journeyId",
        ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
        dailyWorkRequest
    )
}

/**
 * Cancels the journey notification worker for the given journey id.
 * @param context The context to cancel the worker in.
 * @param journeyId The id of the journey.
 */
fun retireJourneyNotificationWorker(context: Context, journeyId: Int) {
    WorkManager.getInstance(context).cancelUniqueWork("JourneyNotificationWorker_$journeyId")
}