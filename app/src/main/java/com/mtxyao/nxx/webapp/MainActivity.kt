package com.mtxyao.nxx.webapp

import android.graphics.Color
import android.os.Build
import android.os.Bundle
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
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : FragmentActivity() {
    var curFragmentPageIndex: Int = 0
    var fragmentPair: List<Pair<String, Fragment>> ? = null
    var mPagerAdapter: MyFragmentPagerAdapter ? = null
    var exitTime: Long = 0

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
                if ((fragmentPair!!.get(curFragmentPageIndex).second as BaseFragment).mAgentWeb!!.handleKeyEvent(keyCode, event)) {
                    return true
                } else {
                    if (System.currentTimeMillis() - exitTime > 2000) {
                        Toast.makeText(this, "再按一次离开", Toast.LENGTH_SHORT).show()
                        exitTime = System.currentTimeMillis()
                    } else {
                        System.exit(0)
                    }
                }
            }
        }
        return true
    }

    private fun hideBottomUIMenu () {
        if (Build.VERSION.SDK_INT in 12..18) {
            val v: View = window.decorView
            v.systemUiVisibility = View.GONE
        } else if (Build.VERSION.SDK_INT >= 19) {
            val decorView: View = window.decorView
            val uiOptions: Int = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;View.SYSTEM_UI_FLAG_FULLSCREEN
            decorView.systemUiVisibility = uiOptions
        }
    }

}
