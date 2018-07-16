package com.mtxyao.nxx.webapp.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mtxyao.nxx.webapp.BaseFragment
import com.mtxyao.nxx.webapp.R

class BoardFragment : BaseFragment(true) {

    override fun getFragmentView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_board, container, false)
    }

    override fun setPageUrl(): String {
        return ""
    }
}