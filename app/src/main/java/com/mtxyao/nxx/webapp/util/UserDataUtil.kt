package com.mtxyao.nxx.webapp.util

import android.content.Context
import com.google.gson.Gson
import java.lang.reflect.Type

object UserDataUtil {
    enum class SharedNames(val value: String) {
        LOGIN_USER_INFO("login_user_info")
    }
    enum class Key(val value: String) {
        USER_ID("user_id"),
        USER_DATA("user_data")
    }

    fun setUserId (context: Context, userId: String) {
        SharedPreferencesTool.addOrUpdate(context, SharedNames.LOGIN_USER_INFO.value, Key.USER_ID.value, userId)
    }

    fun getUserId (context: Context) : String {
        return SharedPreferencesTool.getFromShared(context, SharedNames.LOGIN_USER_INFO.value, Key.USER_ID.value, "")
    }

    fun <T> setUserData (context: Context, userData: T) {
        val dataStr: String = Gson().toJson(userData)
        SharedPreferencesTool.addOrUpdate(context, SharedNames.LOGIN_USER_INFO.value, Key.USER_DATA.value, dataStr)
    }

    fun <T> getUserData (context: Context, classType: Type) : T? {
        var userData: T ? = null
        val userDataStr: String = SharedPreferencesTool.getFromShared(context, SharedNames.LOGIN_USER_INFO.value, Key.USER_DATA.value, "")
        if (userDataStr != "") {
            userData = Gson().fromJson(userDataStr, classType)
        }
        return userData
    }
}