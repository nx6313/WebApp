package com.mtxyao.nxx.webapp.entity

class UserData(type: Int, phone: String, login: Boolean) {
    var cate: Int ? = null // 用户类型
    var tel: String ? = null // 用户手机号
    var needLogin: Boolean ? = null // 每次程序启动到欢迎页面时，判断是否需要登录

    init {
        cate = type
        tel = phone
        needLogin = login
    }
}