package com.mtxyao.nxx.webapp.util

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class MyFragmentPagerAdapter: FragmentPagerAdapter {
    var mFragmentPair: List<Pair<String, Fragment>> ? = null

    constructor(fm: FragmentManager, fragmentPair: List<Pair<String, Fragment>>) : super(fm) {
        mFragmentPair = fragmentPair
    }

    override fun getItem(position: Int): Fragment {
        return mFragmentPair!!.get(position).second
    }

    override fun getCount(): Int {
        return mFragmentPair!!.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentPair!!.get(position).first
    }
}