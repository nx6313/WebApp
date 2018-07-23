package com.mtxyao.nxx.webapp

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.TextView
import com.just.agentweb.*
import com.mtxyao.nxx.webapp.util.AndroidInterfaceForJS
import com.mtxyao.nxx.webapp.util.ComFun
import com.mtxyao.nxx.webapp.util.PageOpt
import pl.droidsonroids.gif.GifImageView

abstract class BaseFragment(webView: Boolean) : Fragment() {
    private var initWebView: Boolean = false
    var mAgentWeb: AgentWeb? = null
    private var titleWrap: View ? = null

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
            val loadingGroup: GifImageView = view.findViewById(R.id.webAppLoading)
            webViewGroup.visibility = View.GONE
            loadingGroup.visibility = View.VISIBLE
            mAgentWeb = AgentWeb.with(this)
                    .setAgentWebParent(webViewGroup, LinearLayout.LayoutParams(-1, -1))
                    .useDefaultIndicator()
                    .setAgentWebWebSettings(AbsAgentWebSettings.getInstance())
                    .setWebChromeClient(MWebChromeClient(titleWrap!!))
                    .setWebViewClient(MWebViewClient(titleWrap!!, loadingGroup, webViewGroup))
                    .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                    .setAgentWebUIController(AgentWebUIControllerImplBase())
                    .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)
                    .interceptUnkownUrl()
                    .createAgentWeb()
                    .ready()
                    .go(setPageUrl() + "?deviceType=android")
            mAgentWeb!!.jsInterfaceHolder.addJavaObject("android", AndroidInterfaceForJS(mAgentWeb!!))
            mAgentWeb!!.agentWebSettings.webSettings.javaScriptEnabled = true
            mAgentWeb!!.agentWebSettings.webSettings.domStorageEnabled = true
            mAgentWeb!!.agentWebSettings.webSettings.loadsImagesAutomatically = true // 支持自动加载图片
            mAgentWeb!!.agentWebSettings.webSettings.useWideViewPort = true // 设置webview推荐使用的窗口，使html界面自适应屏幕
            mAgentWeb!!.agentWebSettings.webSettings.setAppCacheEnabled(true) // 设置APP可以缓存
            mAgentWeb!!.agentWebSettings.webSettings.domStorageEnabled = true // 返回上个界面不刷新  允许本地缓存
            mAgentWeb!!.agentWebSettings.webSettings.allowFileAccess = true // 设置可以访问文件
        }
        return view
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

    class MWebViewClient(titleWrap: View?, loadingGroup: GifImageView?, webViewGroup: ViewGroup?) : WebViewClient() {
        private var tWrap: View ? = null
        private var lGroup: GifImageView ? = null
        private var wGroup: ViewGroup ? = null

        init {
            tWrap = titleWrap
            lGroup = loadingGroup
            wGroup = webViewGroup
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            val title = view!!.title
            tWrap!!.findViewById<TextView>(R.id.pageTitle).text = title
        }

        override fun onPageCommitVisible(view: WebView?, url: String?) {
            super.onPageCommitVisible(view, url)
            val fadeOut = AlphaAnimation(1f, 0f)
            val fadeIn = AlphaAnimation(0f, 1f)
            fadeOut.duration = 400
            fadeIn.duration = 400
            lGroup!!.startAnimation(fadeOut)
            lGroup!!.visibility = View.GONE
            wGroup!!.startAnimation(fadeIn)
            wGroup!!.visibility = View.VISIBLE
        }
    }
    class MWebChromeClient(titleWrap: View?) : WebChromeClient() {
        private var tWrap: View ? = null

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