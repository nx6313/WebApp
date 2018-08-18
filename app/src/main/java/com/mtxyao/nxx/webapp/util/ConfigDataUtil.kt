package com.mtxyao.nxx.webapp.util

import android.content.Context
import com.google.gson.Gson
import com.mtxyao.nxx.webapp.entity.IndexMsg
import org.json.JSONArray
import org.json.JSONObject

object ConfigDataUtil {
    enum class SharedNames(val value: String) {
        INDEX_MSG("index_msg")
    }
    enum class Key(val value: String) {
        MSG_LIST("index_msg_list")
    }

    fun saveIndexMsg (context: Context, dataList: List<IndexMsg>) {
        val dataStr: String = Gson().toJson(dataList)
        SharedPreferencesTool.addOrUpdate(context, "${UserDataUtil.getUserId(context)}_" + SharedNames.INDEX_MSG.value, Key.MSG_LIST.value, dataStr)
    }

    fun getIndexMsg (context: Context) : MutableList<IndexMsg>? {
        var dataList: MutableList<IndexMsg> ? = mutableListOf()
        val indexMsgDataStr: String = SharedPreferencesTool.getFromShared(context, "${UserDataUtil.getUserId(context)}_" + SharedNames.INDEX_MSG.value, Key.MSG_LIST.value, "")
        if (indexMsgDataStr != "") {
            val dataJson = JSONArray(indexMsgDataStr)
            for (i in 0..(dataJson.length() - 1)) {
                val dataObj = JSONObject(dataJson[i].toString())
                val indexMsg = IndexMsg()
                indexMsg.type = dataObj.getString("type")
                indexMsg.time = dataObj.getLong("type")
                when (dataObj.getString("type")) {
                    IndexMsg.TYPE_CLIENT -> {
                        indexMsg.content = dataObj.getString("count")
                    }
                }
                dataList!!.add(indexMsg)
            }
        }
        return dataList
    }
}