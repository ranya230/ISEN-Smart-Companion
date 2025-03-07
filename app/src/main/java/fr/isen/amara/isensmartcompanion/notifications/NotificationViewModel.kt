package fr.isen.amara.isensmartcompanion.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import android.util.Log
import android.graphics.Color
import kotlinx.coroutines.launch
import fr.isen.amara.isensmartcompanion.MainActivity
import fr.isen.amara.isensmartcompanion.R

class NotificationViewModel : ViewModel() {

    fun scheduleNotification(context: Context, eventTitle: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "event_reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Event Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications pour les rappels d'événements"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Rappel d'événement")
            .setContentText("L'événement \"$eventTitle\" approche bientôt!")
            .setSmallIcon(R.drawable.cloche)
            .setColor(Color.parseColor("#B71C1C"))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        GlobalScope.launch(Dispatchers.Main) {
            delay(10_000) // 🔄 Notification après 10 secondes
            if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                NotificationManagerCompat.from(context).notify(eventTitle.hashCode(), notification)
            } else {
                Log.e("Notification", "Permission de notification refusée")
            }

        }
    }
}
