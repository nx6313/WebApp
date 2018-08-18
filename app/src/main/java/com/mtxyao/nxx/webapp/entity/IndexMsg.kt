package com.mtxyao.nxx.webapp.entity

class IndexMsg {
    companion object {
        var TYPE_CLIENT = "1" // 潜客跟进
    }

    var type: String ? = null
    var content: String ? = null
    var time: Long ? = null
}