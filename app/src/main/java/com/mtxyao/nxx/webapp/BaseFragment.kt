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
import com.just.agentweb.*

abstract class BaseFragment(webView: Boolean) : Fragment() {
    var initWebView: Boolean = false
    var mAgentWeb: AgentWeb? = null
    var webViewClient : WebViewClient = MWebViewClient()
    var webChromeClient : WebChromeClient = MWebChromeClient()

    init {
        initWebView = webView
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = getFragmentView(inflater, container)
        if (initWebView) {
            val webViewGroup: ViewGroup = view.findViewById(R.id.webAppMain)
            mAgentWeb = AgentWeb.with(this)
                    .setAgentWebParent(webViewGroup, LinearLayout.LayoutParams(-1, -1))
                    .useDefaultIndicator()
                    .setAgentWebWebSettings(AbsAgentWebSettings.getInstance())
                    .setWebChromeClient(webChromeClient)
                    .setWebViewClient(webViewClient)
                    .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                    .setAgentWebUIController(AgentWebUIControllerImplBase())
                    .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)
                    .interceptUnkownUrl()
                    .createAgentWeb()
                    .ready()
                    .go(setPageUrl())
            mAgentWeb!!.agentWebSettings.webSettings.javaScriptEnabled = true
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
    open fun setPageUrl(): String {
        return ""
    }

    class MWebViewClient : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }
    }
    class MWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
        }
    }

}