package com.mtxyao.nxx.webapp.util

import android.content.Context
import com.google.gson.Gson
import com.mtxyao.nxx.webapp.entity.IndexMsg
import org.json.JSONArray
import org.json.JSONObject

object ConfigDataUtil {
    enum class SharedNames(val value: String) {
        RECENTLY_PIC("recently_pic"),
        INDEX_MSG("index_msg")
    }
    enum class Keys(val value: String) {
        MSG_LIST("index_msg_list"),
        RECENTLY_LIST("recently_pic_list")
    }

    fun saveRecentlyPic (context: Context, dataList: MutableList<Pair<Long, String>>) {
        val dataStr: String = Gson().toJson(dataList)
        SharedPreferencesTool.addOrUpdate(context, "${UserDataUtil.getUserId(context)}_" + SharedNames.RECENTLY_PIC.value, Keys.RECENTLY_LIST.value, dataStr)
    }

    fun getRecentlyPic (context: Context) : MutableList<Pair<Long, String>>? {
        var dataList: MutableList<Pair<Long, String>> ? = mutableListOf()
        val recentlyPicStr: String = SharedPreferencesTool.getFromShared(context, "${UserDataUtil.getUserId(context)}_" + SharedNames.RECENTLY_PIC.value, Keys.RECENTLY_LIST.value, "")
        if (recentlyPicStr != "") {
            val dataJson = JSONArray(recentlyPicStr)
            for (i in 0..(dataJson.length() - 1)) {
                val dataObj = JSONObject(dataJson[i].toString())

                dataList!!.add(Pair(dataObj.getLong("first"), dataObj.getString("second")))
            }
        }
        return dataList
    }

    fun saveIndexMsg (context: Context, dataList: List<IndexMsg>) {
        val dataStr: String = Gson().toJson(dataList)
        SharedPreferencesTool.addOrUpdate(context, "${UserDataUtil.getUserId(context)}_" + SharedNames.INDEX_MSG.value, Keys.MSG_LIST.value, dataStr)
    }

    fun getIndexMsg (context: Context) : MutableList<IndexMsg>? {
        var dataList: MutableList<IndexMsg> ? = mutableListOf()
        val indexMsgDataStr: String = SharedPreferencesTool.getFromShared(context, "${UserDataUtil.getUserId(context)}_" + SharedNames.INDEX_MSG.value, Keys.MSG_LIST.value, "")
        if (indexMsgDataStr != "") {
            val dataJson = JSONArray(indexMsgDataStr)
            for (i in 0..(dataJson.length() - 1)) {
                val dataObj = JSONObject(dataJson[i].toString())
                val indexMsg = IndexMsg()

                indexMsg.type = dataObj.getInt("type")
                if (dataObj.has("id")) {
                    indexMsg.id = dataObj.getInt("id")
                }
                if (dataObj.has("count")) {
                    indexMsg.count = dataObj.getInt("count")
                }
                if (dataObj.has("title")) {
                    indexMsg.title = dataObj.getString("title")
                }
                if (dataObj.has("time")) {
                    indexMsg.time = dataObj.getLong("time")
                }

                dataList!!.add(indexMsg)
            }
        }
        return dataList
    }

    fun clearData(context: Context, shareName: String, key: String) {
        SharedPreferencesTool.deleteFromShared(context, "${UserDataUtil.getUserId(context)}_" + shareName, key)
    }
}