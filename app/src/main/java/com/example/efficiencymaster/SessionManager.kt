package com.example.efficiencymaster

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    fun userLogin(usernname: String) {
        val editor = prefs.edit()
        editor.putString("username", usernname)
        editor.apply()
    }

    fun isUserLoggedIn(): Boolean {
        return prefs.contains("username")
    }

   val getUser: String
        get() = prefs.getString("username", null).toString()


    fun logOut() {
        val editor = prefs.edit()
        editor.remove("username")
        editor.apply()
    }
}