package com.mtxyao.nxx.webapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient

abstract class BaseFragment : Fragment() {
    var mAgentWeb: AgentWeb? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = getFragmentView(inflater, container)
        val webViewGroup: ViewGroup = view.findViewById(R.id.webAppMain)
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(webViewGroup, LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)
                .interceptUnkownUrl()
                .createAgentWeb()
                .ready()
                .go(setPageUrl())
        return view
    }

    fun callByAndroid (jsFunName: String) {
        mAgentWeb!!.jsAccessEntrace.quickCallJs(jsFunName)
    }

    override fun onPause() {
        mAgentWeb!!.webLifeCycle.onPause()
        super.onPause()
    }

    override fun onResume() {
        mAgentWeb!!.webLifeCycle.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        mAgentWeb!!.webLifeCycle.onDestroy()
        super.onDestroy()
    }

    abstract fun getFragmentView(inflater: LayoutInflater, container: ViewGroup?): View
    open fun setPageUrl(): String {
        return "";
    }

}