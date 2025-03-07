package fr.isen.amara.isensmartcompanion.notifications

import android.content.Context

object SharedPreferencesManager {
    private const val PREFERENCE_NAME = "event_preferences"
    private const val KEY_NOTIFICATION_PREFIX = "notification_"

    fun isNotificationSubscribed(context: Context, eventTitle: String): Boolean {
        val preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        return preferences.getBoolean(KEY_NOTIFICATION_PREFIX + eventTitle, false)
    }

    fun setNotificationSubscribed(context: Context, eventTitle: String, isSubscribed: Boolean) {
        val preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean(KEY_NOTIFICATION_PREFIX + eventTitle, isSubscribed)
        editor.apply()
    }
}
