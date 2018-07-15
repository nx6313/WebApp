package com.mtxyao.nxx.webapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.mtxyao.nxx.webapp.fragments.IndexFragment
import com.mtxyao.nxx.webapp.fragments.SecondFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : FragmentActivity() {
    var curFragmentPageIndex: Int = 0
    var fragmentPair: List<Pair<String, Fragment>> ? = null
    var mPagerAdapter: MyFragmentPagerAdapter ? = null
    var exitTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentPair = listOf(
                Pair("IndexFragment", IndexFragment()),
                Pair("SecondFragment", SecondFragment())
        )
        mPagerAdapter = MyFragmentPagerAdapter(supportFragmentManager, fragmentPair!!)
        viewPager.adapter = mPagerAdapter
        viewPager.currentItem = curFragmentPageIndex
        viewPager.noScroll = true
        viewPager.noCutAnimation = true
    }

    fun toMenu (view: View) {
        when (view.tag) {
            "menu-index" -> curFragmentPageIndex = 0
            "menu-ss" -> curFragmentPageIndex = 1
        }
        viewPager.currentItem = curFragmentPageIndex
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

}
