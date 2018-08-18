package com.mtxyao.nxx.webapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.mtxyao.nxx.webapp.fragments.ConsoleFragment
import com.mtxyao.nxx.webapp.fragments.BoardFragment
import com.mtxyao.nxx.webapp.fragments.WinnersFragment
import com.mtxyao.nxx.webapp.fragments.ClientFragment
import com.mtxyao.nxx.webapp.fragments.MeFragment
import com.mtxyao.nxx.webapp.util.ComFun
import com.mtxyao.nxx.webapp.util.JpushReceiver
import com.mtxyao.nxx.webapp.util.MyFragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : FragmentActivity() {
    private var curFragmentPageIndex: Int = 0
    private var fragmentPair: List<Pair<String, Fragment>> ? = null
    private var mPagerAdapter: MyFragmentPagerAdapter? = null
    private var exitTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hideBottomUIMenu()
        toggleCurMenuIcon()

        fragmentPair = listOf(
                Pair("menu-console", ConsoleFragment()),
                Pair("menu-board", BoardFragment()),
                Pair("menu-winners", WinnersFragment()),
                Pair("menu-client", ClientFragment()),
                Pair("menu-me", MeFragment())
        )
        mPagerAdapter = MyFragmentPagerAdapter(supportFragmentManager, fragmentPair!!)
        viewPager.adapter = mPagerAdapter
        viewPager.currentItem = curFragmentPageIndex
        viewPager.noScroll = true
        viewPager.noCutAnimation = true
        viewPager.offscreenPageLimit = fragmentPair!!.size

        val mainReceiver = MainReceiver(fragmentPair)
        val filter = IntentFilter()
        filter.addAction(JpushReceiver.BCS_ACTION_INDEX_MSG)
        registerReceiver(mainReceiver, filter)
    }

    fun toMenu (view: View) {
        when (view.tag) {
            "menu-console" -> curFragmentPageIndex = 0
            "menu-board" -> curFragmentPageIndex = 1
            "menu-winners" -> curFragmentPageIndex = 2
            "menu-client" -> curFragmentPageIndex = 3
            "menu-me" -> curFragmentPageIndex = 4
        }
        viewPager.currentItem = curFragmentPageIndex
        toggleCurMenuIcon()
        (fragmentPair!![curFragmentPageIndex].second as BaseFragment).sendWebActivatedEvent()
    }

    private fun toggleCurMenuIcon () {
        for (menuIndex in 0..(footerMenu.childCount - 1)) {
            val menuImg : ImageView = (footerMenu.getChildAt(menuIndex) as ViewGroup).getChildAt(0) as ImageView
            val menuTxt : TextView = (footerMenu.getChildAt(menuIndex) as ViewGroup).getChildAt(1) as TextView
            when (menuIndex) {
                0 -> menuImg.setImageResource(R.drawable.menu_1_normal)
                1 -> menuImg.setImageResource(R.drawable.menu_2_normal)
                2 -> menuImg.setImageResource(R.drawable.menu_3_normal)
                3 -> menuImg.setImageResource(R.drawable.menu_4_normal)
                4 -> menuImg.setImageResource(R.drawable.menu_5_normal)
            }
            menuTxt.setTextColor(Color.parseColor("#404040"))
        }
        val curMenuImg : ImageView = (footerMenu.getChildAt(curFragmentPageIndex) as ViewGroup).getChildAt(0) as ImageView
        val curMenuTxt : TextView = (footerMenu.getChildAt(curFragmentPageIndex) as ViewGroup).getChildAt(1) as TextView
        when (curFragmentPageIndex) {
            0 -> curMenuImg.setImageResource(R.drawable.menu_1)
            1 -> curMenuImg.setImageResource(R.drawable.menu_2)
            2 -> curMenuImg.setImageResource(R.drawable.menu_3)
            3 -> curMenuImg.setImageResource(R.drawable.menu_4)
            4 -> curMenuImg.setImageResource(R.drawable.menu_5)
        }
        curMenuTxt.setTextColor(Color.parseColor("#0f86d6"))
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when(keyCode) {
            KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_DOWN -> {
                if ((fragmentPair!![curFragmentPageIndex].second as BaseFragment).mAgentWeb != null && (fragmentPair!![curFragmentPageIndex].second as BaseFragment).mAgentWeb!!.handleKeyEvent(keyCode, event)) {
                    return true
                } else {
                    if (System.currentTimeMillis() - exitTime > 2000) {
                        ComFun.showToast(this, "再按一次离开", Toast.LENGTH_SHORT)
                        exitTime = System.currentTimeMillis()
                    } else {
                        System.exit(0)
                    }
                    // 实现只在冷启动时显示启动页，即点击返回键与点击HOME键退出效果一致
//                    val intent = Intent(Intent.ACTION_MAIN)
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    intent.addCategory(Intent.CATEGORY_HOME)
//                    startActivity(intent)
//                    return true
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            BaseFragment.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(Intent.ACTION_GET_CONTENT, null)
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                    startActivityForResult(intent, BaseFragment.PICKER_PIC)
                } else {
                    ComFun.showToast(this, "您拒绝了选取图片的权限", Toast.LENGTH_SHORT)
                }
            }
        }
    }

    private fun hideBottomUIMenu () {
        if (Build.VERSION.SDK_INT in 12..18) {
            val v: View = window.decorView
            v.systemUiVisibility = View.GONE
        } else if (Build.VERSION.SDK_INT >= 19) {
            val decorView: View = window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    class MainReceiver(fragmentPair: List<Pair<String, Fragment>> ?) : BroadcastReceiver() {
        val fPair: List<Pair<String, Fragment>> ? = fragmentPair
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent!!.action) {
                JpushReceiver.BCS_ACTION_INDEX_MSG -> {
                    val data = intent.getStringExtra("data")
                    if (data != null) {
                        val dataJson = JSONObject(data)
                        (fPair!![0].second as ConsoleFragment).initToDoListFromReceiver(dataJson)
                    }
                }
            }
        }
    }

}
