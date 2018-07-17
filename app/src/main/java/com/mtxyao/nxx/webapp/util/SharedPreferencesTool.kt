package com.mtxyao.nxx.webapp.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesTool {
    fun addOrUpdate (context: Context, sharedName: String, key: String, value: String) {
        val mSharedPreferences: SharedPreferences = context.getSharedPreferences(sharedName, Activity.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putString(key, value)
        editor.commit()
    }

    fun addOrUpdate (context: Context, sharedName: String, key: String, value: Int) {
        val mSharedPreferences: SharedPreferences = context.getSharedPreferences(sharedName, Activity.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    fun addOrUpdate (context: Context, sharedName: String, key: String, value: Boolean) {
        val mSharedPreferences: SharedPreferences = context.getSharedPreferences(sharedName, Activity.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.commit()
    }

    fun getFromShared (context: Context, sharedName: String, key: String, defValue: Boolean) : Boolean {
        val mSharedPreferences: SharedPreferences = context.getSharedPreferences(sharedName, Activity.MODE_PRIVATE)
        return mSharedPreferences.getBoolean(key, defValue)
    }

    fun getFromShared (context: Context, sharedName: String, key: String, defValue: Int) : Int {
        val mSharedPreferences: SharedPreferences = context.getSharedPreferences(sharedName, Activity.MODE_PRIVATE)
        return mSharedPreferences.getInt(key, defValue)
    }

    fun getFromShared (context: Context, sharedName: String, key: String, defValue: String) : String {
        val mSharedPreferences: SharedPreferences = context.getSharedPreferences(sharedName, Activity.MODE_PRIVATE)
        return mSharedPreferences.getString(key, defValue)
    }

    fun getFromShared (context: Context, sharedName: String) : MutableMap<String, *>? {
        val mSharedPreferences: SharedPreferences = context.getSharedPreferences(sharedName, Activity.MODE_PRIVATE)
        return mSharedPreferences.all
    }

    fun clearShared (context: Context, sharedNames: Array<String>) {
        if (sharedNames.isNotEmpty()) {
            for (sharedName: String in sharedNames) {
                val mSharedPreferences: SharedPreferences = context.getSharedPreferences(sharedName, Activity.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = mSharedPreferences.edit()
                editor.clear()
                editor.commit()
            }
        }
    }

    fun clearShared (context: Context, sharedName: String) {
        val mSharedPreferences: SharedPreferences = context.getSharedPreferences(sharedName, Activity.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.clear()
        editor.commit()
    }

    fun deleteFromShared (context: Context, sharedName: String, key: String) {
        val mSharedPreferences: SharedPreferences = context.getSharedPreferences(sharedName, Activity.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.remove(key)
        editor.commit()
    }
}