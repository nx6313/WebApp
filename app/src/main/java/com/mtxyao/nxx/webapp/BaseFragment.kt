package com.mtxyao.nxx.webapp

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.TextView
import com.just.agentweb.*
import com.mtxyao.nxx.webapp.util.AndroidInterfaceForJS
import com.mtxyao.nxx.webapp.util.ComFun
import com.mtxyao.nxx.webapp.util.PageOpt

abstract class BaseFragment(webView: Boolean) : Fragment() {
    var initWebView: Boolean = false
    var mAgentWeb: AgentWeb? = null
    var titleWrap: View ? = null

    init {
        initWebView = webView
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = getFragmentView(inflater, container)
        if (initWebView) {
            titleWrap = view.findViewById(R.id.pageTitleWrap)
            titleWrap!!.setPadding(0, ComFun.getStateBarHeight(), 0, 0)
            val pageOpt = getPageOpt()
            if (!pageOpt.showTitleBar) {
                titleWrap!!.visibility = View.GONE
            }
            val webViewGroup: ViewGroup = view.findViewById(R.id.webAppMain)
            mAgentWeb = AgentWeb.with(this)
                    .setAgentWebParent(webViewGroup, LinearLayout.LayoutParams(-1, -1))
                    .useDefaultIndicator()
                    .setAgentWebWebSettings(AbsAgentWebSettings.getInstance())
                    .setWebChromeClient(MWebChromeClient(titleWrap!!))
                    .setWebViewClient(MWebViewClient(titleWrap!!))
                    .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                    .setAgentWebUIController(AgentWebUIControllerImplBase())
                    .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)
                    .interceptUnkownUrl()
                    .createAgentWeb()
                    .ready()
                    .go(setPageUrl() + "?deviceType=android")
            mAgentWeb!!.jsInterfaceHolder.addJavaObject("android", AndroidInterfaceForJS())
            mAgentWeb!!.agentWebSettings.webSettings.javaScriptEnabled = true
            mAgentWeb!!.agentWebSettings.webSettings.domStorageEnabled = true
            callByAndroid("callByAndroid")
        }
        return view
    }

    fun callByAndroid (jsFunName: String) {
        if (initWebView) {
            mAgentWeb!!.jsAccessEntrace.quickCallJs(jsFunName)
        }
    }

    override fun onPause() {
        if (initWebView) {
            mAgentWeb!!.webLifeCycle.onPause()
        }
        super.onPause()
    }

    override fun onResume() {
        if (initWebView) {
            mAgentWeb!!.webLifeCycle.onResume()
        }
        super.onResume()
    }

    override fun onDestroy() {
        if (initWebView) {
            mAgentWeb!!.webLifeCycle.onDestroy()
        }
        super.onDestroy()
    }

    abstract fun getFragmentView(inflater: LayoutInflater, container: ViewGroup?): View
    open fun getPageOpt(): PageOpt {
        return PageOpt()
    }
    open fun setPageUrl(): String {
        return ""
    }

    class MWebViewClient(titleWrap: View?) : WebViewClient() {
        var tWrap: View ? = null

        init {
            tWrap = titleWrap
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            val title = view!!.title
            tWrap!!.findViewById<TextView>(R.id.pageTitle).text = title
        }
    }
    class MWebChromeClient(titleWrap: View?) : WebChromeClient() {
        var tWrap: View ? = null

        init {
            tWrap = titleWrap
        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
        }

        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            tWrap!!.findViewById<TextView>(R.id.pageTitle).text = title
        }
    }

}