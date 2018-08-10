package com.mtxyao.nxx.webapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.mtxyao.nxx.webapp.entity.UserData
import com.mtxyao.nxx.webapp.util.ComFun
import com.mtxyao.nxx.webapp.util.Urls
import com.mtxyao.nxx.webapp.util.UserDataUtil
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val userData: UserData? = UserDataUtil.getUserData(this)
        if (userData != null) {
            if (userData.needLogin!!) etUserLoginPhone.text = Editable.Factory.getInstance().newEditable(userData!!.user!!.phone)
        }
    }

    fun toLogin (view: View) {
        val userLoginPhone: String = etUserLoginPhone.text.toString()
        val userLoginPwd: String = etUserLoginPwd.text.toString()
        when {
            userLoginPhone == "" -> ComFun.showToast(this, "手机号不能为空", Toast.LENGTH_SHORT)
            userLoginPwd == "" -> ComFun.showToast(this, "登录密码不能为空", Toast.LENGTH_SHORT)
            else -> {
                ComFun.closeIME(this, loginWrapLayout)
                ComFun.showLoading(this, "登录中，请稍后", true)
                OkGo.post<String>(Urls.URL_BEFORE + Urls.URL_LOGIN)
                        .upJson(JSONObject().put("phone", userLoginPhone.trim()).put("password", userLoginPwd.trim()))
                        .tag(this).execute(object: StringCallback() {
                            override fun onSuccess(response: Response<String>?) {
                                val data = JSONObject(response!!.body())
                                if (data.has("code") && data.getString("code") == "1") {
                                    ComFun.showToast(this@LoginActivity, "登录成功", Toast.LENGTH_SHORT)
                                    val userData = UserData(System.currentTimeMillis(), ((data["obj"] as JSONObject).getString("createDate") + "000").toLong(), false, Gson().fromJson((data["obj"] as JSONObject).toString(), UserData.UserInfo::class.java))
                                    UserDataUtil.setUserData(this@LoginActivity, userData)
                                    UserDataUtil.setUserId(this@LoginActivity, userData.user!!.id!!)

                                    val mainIntent = Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(mainIntent)
                                    finish()
                                } else {
                                    ComFun.showToast(this@LoginActivity, data.getString("message"), Toast.LENGTH_SHORT)
                                }
                            }

                            override fun onError(response: Response<String>?) {
                                ComFun.showToast(this@LoginActivity, "登录失败", Toast.LENGTH_SHORT)
                                super.onError(response)
                            }

                            override fun onFinish() {
                                ComFun.hideLoading()
                                super.onFinish()
                            }
                        })
            }
        }
    }
}
