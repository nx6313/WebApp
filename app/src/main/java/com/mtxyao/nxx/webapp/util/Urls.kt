package com.mtxyao.nxx.webapp.util

object Urls {
    /**
     * web地址前缀
     */
    const val WEB_BEFORE: String = "http://172.18.168.67:8080/"
//    const val WEB_BEFORE: String = "http://m.dachangjr.com/"
    /**
     * 接口前缀
     */
//    const val URL_BEFORE: String = "http://172.18.168.44:8080/"
    const val URL_BEFORE: String = "http://wx.dcsc520.cn/appapi-0.0.1-SNAPSHOT/"
    /**
     * 登录接口
     */
    const val URL_LOGIN: String = "user/login"
    /**
     * 根据用户id获取用户信息 - 售前
     */
    const val URL_GET_USERINFI_BY_ID_BEFORE: String = "data/senior/consultant/"
    /**
     * 根据用户id获取用户信息 - 售后
     */
    const val URL_GET_USERINFI_BY_ID_AFTER: String = "after/senior/consultant/"
    /**
     * 文件上传接口
     */
    const val URL_FILE_UPLOAD: String = "fileUpload/headImageUpload"
    /**
     * 获取工作台通知消息
     */
    const val URL_INFORM_MSG: String = "messageController/findAll"
    /**
     * 获取工作台个人待处理消息
     */
    const val URL_INFORM_DBSY: String = "messageController/sendMsg"
}