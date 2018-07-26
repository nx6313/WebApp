package com.mtxyao.nxx.webapp.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mtxyao.nxx.webapp.BaseFragment
import com.mtxyao.nxx.webapp.R
import com.mtxyao.nxx.webapp.util.PageOpt
import com.mtxyao.nxx.webapp.util.Urls

class BoardFragment : BaseFragment(true) {

    override fun getFragmentView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_board, container, false)
    }

    override fun getPageOpt(): PageOpt {
        return PageOpt().setShowTitleBar(true).setCanRef(true)
    }

    override fun setPageUrl(): String {
        return "${Urls.WEB_BEFORE}#/app-home"
    }
}