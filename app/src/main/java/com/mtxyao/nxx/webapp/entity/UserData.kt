package com.mtxyao.nxx.webapp.entity

class UserData(login: Long, base: Long, need: Boolean, userInfo: UserInfo) {
    var loginDate: Long ? = null // 用户登录时间
    var basedate: Long ? = null // 用户数据基础日期
    var needLogin: Boolean ? = null // 每次程序启动到欢迎页面时，判断是否需要登录
    var user: UserInfo ? = null // 用户信息

    init {
        loginDate = login
        basedate = base
        needLogin = need
        user = userInfo
    }

    class UserInfo {
        var args: Any ? = null
        var children: Array<Any> ? = null
        var companyId: Int ? = null
        var companyName: String ? = null
        var createBy: Int ? = null
        var createDate: Long ? = null
        var delFlag: String ? = null
        var duty: String ? = null
        var dutyName: String ? = null
        var grade: String ? = null
        var id: Int ? = null
        var name: String ? = null
        var parentId: Int ? = null
        var password: String ? = null
        var phone: String ? = null
        var photo: String ? = null
        var remarks: String ? = null
        var scope: String ? = null
        var updateBy: Int ? = null
        var updateDate: Long ? = null
    }
}