package android.example.com.notificationscheduler

import android.app.job.JobParameters
import android.app.job.JobService
import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.Context
import android.graphics.Color
import android.app.PendingIntent
import android.content.Intent
import android.support.v4.app.NotificationCompat


class NotificationJobService: JobService() {

    companion object {
        // Notification channel ID.
        private val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    }

    var mNotifyManager: NotificationManager? = null

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        //Create the notification channel
        createNotificationChannel()

        //Set up the notification content intent to launch the app when clicked
        val contentPendingIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
            .setContentTitle("Job Service")
            .setContentText("Your Job ran to completion!")
            .setContentIntent(contentPendingIntent)
            .setSmallIcon(R.drawable.ic_job_running)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)

        mNotifyManager?.notify(0, builder.build())
        return false
    }

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    fun createNotificationChannel() {

        // Define notification manager object.
        mNotifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create the NotificationChannel with all the parameters.
            val notificationChannel = NotificationChannel(PRIMARY_CHANNEL_ID, "Job Service notification", NotificationManager.IMPORTANCE_HIGH)

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Notifications from Job Service"

            mNotifyManager?.createNotificationChannel(notificationChannel)
        }
    }

}