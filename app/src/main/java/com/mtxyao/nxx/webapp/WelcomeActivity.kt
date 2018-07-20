package com.mtxyao.nxx.webapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.mtxyao.nxx.webapp.entity.UserData
import com.mtxyao.nxx.webapp.util.UserDataUtil

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val userData: UserData ? = UserDataUtil.getUserData(this)
        Handler().postDelayed({
            if (userData == null) {
                val bannerIntent = Intent(this, BannerActivity::class.java)
                startActivity(bannerIntent)
                finish()
            } else {
                if (userData?.needLogin!! || userData.loginDate!! - System.currentTimeMillis() > 10 * 24 * 60 * 60 * 1000) {
                    val loginIntent = Intent(this, LoginActivity::class.java)
                    startActivity(loginIntent)
                    finish()
                } else {
                    val mainIntent = Intent(this, MainActivity::class.java)
                    startActivity(mainIntent)
                    finish()
                }
            }
        }, 1200)
    }
}