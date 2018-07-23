package com.mtxyao.nxx.webapp.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mtxyao.nxx.webapp.BaseFragment
import com.mtxyao.nxx.webapp.R

class MeFragment : BaseFragment(true) {

    override fun getFragmentView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_me, container, false)
    }

    override fun setPageUrl(): String {
        return "http://172.18.168.67:8080/#/app-me"
    }
}