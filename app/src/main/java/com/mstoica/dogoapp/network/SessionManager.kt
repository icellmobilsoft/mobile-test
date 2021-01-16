package com.mstoica.dogoapp.network

import android.content.Context
import android.content.SharedPreferences
import com.mstoica.dogoapp.R
import com.mstoica.dogoapp.commons.utils.SingletonHolder
import org.jetbrains.anko.defaultSharedPreferences
import java.util.*

class SessionManager private constructor(context: Context) {

    val userName: String

    init {
        val persistedUserName = context.defaultSharedPreferences.getString(KEY_USER_NAME, null)

        if (persistedUserName == null) {
            userName = generateNewUserName(context)
            context.defaultSharedPreferences.edit().putString(KEY_USER_NAME, userName).apply()
        } else {
            userName = persistedUserName
        }
    }

    private fun generateNewUserName(context: Context): String {
        val uniqueID = UUID.randomUUID().toString()
        return context.getString(R.string.user_name, uniqueID)
    }

    companion object: SingletonHolder<SessionManager, Context>(::SessionManager) {
        const val KEY_USER_NAME = "KEY_USER_NAME"
    }
}