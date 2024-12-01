package hu.bme.aut.qrvhfq.EnchantedEmporium.util

import android.content.Context

class SharedPrefsUtil (private val context: Context){
    private val PREFS_NAME = "user_prefs"
    private val KEY_LOGIN_TIMESTAMP = "login_timestamp"

    fun saveLoginTimestamp(context: Context) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val currentTime = System.currentTimeMillis()
        editor.putLong(KEY_LOGIN_TIMESTAMP, currentTime)
        editor.apply()
    }

    fun shouldAutoLogin(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastLoginTime = sharedPref.getLong(KEY_LOGIN_TIMESTAMP, 0)

        if (lastLoginTime == 0L) return false

        val currentTime = System.currentTimeMillis()
        val timeDiff = currentTime - lastLoginTime
        return timeDiff <= 14400000
    }
    fun clearPreferences() {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }
}