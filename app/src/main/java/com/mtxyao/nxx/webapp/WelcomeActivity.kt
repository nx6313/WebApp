package com.mtxyao.nxx.webapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.mtxyao.nxx.webapp.entity.UserData
import com.mtxyao.nxx.webapp.util.ComFun
import com.mtxyao.nxx.webapp.util.ConfigDataUtil
import com.mtxyao.nxx.webapp.util.UserDataUtil

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val userData: UserData ? = UserDataUtil.getUserData(this)
        initAppData()
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
                    ComFun.initJPushServer(this)
                    val mainIntent = Intent(this, MainActivity::class.java)
                    startActivity(mainIntent)
                    finish()
                }
            }
        }, 1200)
    }

    private fun initAppData () {
        // 获取最近的图片
        Thread(Runnable {
            if (ContextCompat.checkSelfPermission(this@WelcomeActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@WelcomeActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), BaseWebActivity.REQUEST_PERMISSION_READ_EXTERNAL_STORAGE)
            } else {
                val recentlyImages = ComFun.getRecentlyPhotoPath(this@WelcomeActivity, 6)
                if (recentlyImages.size > 0) {
                    ConfigDataUtil.saveRecentlyPic(this@WelcomeActivity, recentlyImages)
                } else {
                    ConfigDataUtil.clearData(this@WelcomeActivity, ConfigDataUtil.SharedNames.RECENTLY_PIC.value, ConfigDataUtil.Keys.RECENTLY_LIST.value)
                }
            }
        }).start()
    }
}