package com.mtxyao.nxx.webapp.util

import android.content.Context
import com.google.gson.Gson
import com.mtxyao.nxx.webapp.entity.UserData
import org.json.JSONObject

object UserDataUtil {
    enum class SharedNames(val value: String) {
        LOGIN_USER_INFO("login_user_info")
    }
    enum class Key(val value: String) {
        USER_ID("user_id"),
        USER_DATA("user_data")
    }

    fun setUserId (context: Context, userId: Int) {
        SharedPreferencesTool.addOrUpdate(context, SharedNames.LOGIN_USER_INFO.value, Key.USER_ID.value, userId)
    }

    fun getUserId (context: Context) : Int {
        return SharedPreferencesTool.getFromShared(context, SharedNames.LOGIN_USER_INFO.value, Key.USER_ID.value, -1)
    }

    fun setUserData (context: Context, userData: UserData) {
        val dataStr: String = Gson().toJson(userData)
        SharedPreferencesTool.addOrUpdate(context, SharedNames.LOGIN_USER_INFO.value, Key.USER_DATA.value, dataStr)
    }

    fun getUserData (context: Context) : UserData? {
        var userData: UserData ? = null
        val userDataStr: String = SharedPreferencesTool.getFromShared(context, SharedNames.LOGIN_USER_INFO.value, Key.USER_DATA.value, "")
        if (userDataStr != "") {
            val userDataJson = JSONObject(userDataStr)
            userData = UserData(
                    userDataJson.getLong("loginDate"),
                    userDataJson.getLong("basedate"),
                    userDataJson.getBoolean("needLogin"),
                    Gson().fromJson(userDataJson.getJSONObject("user").toString(), UserData.UserInfo::class.java)
            )
        }
        return userData
    }
}