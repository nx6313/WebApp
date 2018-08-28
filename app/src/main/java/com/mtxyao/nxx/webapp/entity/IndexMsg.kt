package com.mtxyao.nxx.webapp.entity

import com.mtxyao.nxx.webapp.R

class IndexMsg {
    // "id":7,"noticeContent":"通知内容 。。。","noticeId":null,
    // "noticeTittle":null,"companyId":13,"status":0,"type":1,
    // "typeValue":null,"createDate":"2018-07-30 10:11:21","createBy":null,"delFlag":1,"readFlag":0
    companion object {
        var TYPE_CLIENT = 1 // 潜客跟进
        var toDoListTypeToIcon = mapOf(
                -1 to R.drawable.icon_todo,
                TYPE_CLIENT to R.drawable.icon_crm,
                2 to R.drawable.icon_todo
        )
    }

    var id: Int ? = null
    var type: Int ? = null
    var title: String ? = null
    var count: Int ? = null
    var time: Long ? = null
}