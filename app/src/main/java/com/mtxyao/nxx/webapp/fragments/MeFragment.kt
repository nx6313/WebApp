package com.mtxyao.nxx.webapp.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mtxyao.nxx.webapp.BaseFragment
import com.mtxyao.nxx.webapp.R
import com.mtxyao.nxx.webapp.entity.UserData
import com.mtxyao.nxx.webapp.util.CircleImageView
import com.mtxyao.nxx.webapp.util.PageOpt
import com.mtxyao.nxx.webapp.util.Urls
import com.mtxyao.nxx.webapp.util.UserDataUtil

class MeFragment : BaseFragment(true) {

    override fun getFragmentView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_me, container, false)
    }

    override fun getPageOpt(): PageOpt {
        return PageOpt().setShowTitleBar(false)
    }

    override fun setPageUrl(): String {
        return "${Urls.WEB_BEFORE}#/app-me"
    }

}