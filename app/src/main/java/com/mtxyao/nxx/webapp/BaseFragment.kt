package com.mtxyao.nxx.webapp

import android.annotation.TargetApi
import android.app.Activity.RESULT_OK
import android.content.ContentUris
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.just.agentweb.*
import com.mtxyao.nxx.webapp.util.AndroidInterfaceForJS
import com.mtxyao.nxx.webapp.util.ComFun
import com.mtxyao.nxx.webapp.util.MyApplication
import com.mtxyao.nxx.webapp.util.PageOpt
import com.yalantis.ucrop.UCrop
import pl.droidsonroids.gif.GifImageView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseFragment(webView: Boolean) : Fragment() {
    private var initWebView: Boolean = false
    var mAgentWeb: AgentWeb? = null
    private var titleWrap: View ? = null
    private var statusBar: View ? = null
    companion object {
        open var PICKER_PIC: Int = 1
        open var PICKER_PHOTO: Int = 2
        open var imageUri: Uri ? = null
    }

    init {
        initWebView = webView
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = getFragmentView(inflater, container)
        if (initWebView) {
            titleWrap = view.findViewById(R.id.pageTitleWrap)
            titleWrap!!.findViewById<ImageView>(R.id.pageBack).setOnClickListener {
                mAgentWeb!!.back()
                if (!mAgentWeb!!.webCreator.webView.canGoBack()) {
                    it.visibility = View.GONE
                }
            }
            statusBar = view.findViewById(R.id.statusBar)
            statusBar!!.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ComFun.getStateBarHeight())
            val pageOpt = getPageOpt()
            if (!pageOpt.showTitleBar) {
                statusBar!!.visibility = View.GONE
                titleWrap!!.visibility = View.GONE
            }
            val webViewGroup: ViewGroup = view.findViewById(R.id.webAppMain)
            val loadingGroup: GifImageView = view.findViewById(R.id.webAppLoading)
            loadingGroup.visibility = View.GONE
            mAgentWeb = AgentWeb.with(this)
                    .setAgentWebParent(webViewGroup, LinearLayout.LayoutParams(-1, -1))
                    .useDefaultIndicator()
                    .setAgentWebWebSettings(AbsAgentWebSettings.getInstance())
                    .setWebChromeClient(MWebChromeClient(titleWrap!!, loadingGroup, webViewGroup))
                    .setWebViewClient(MWebViewClient(titleWrap!!, loadingGroup, webViewGroup))
                    .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                    .setAgentWebUIController(AgentWebUIControllerImplBase())
                    .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)
                    .interceptUnkownUrl()
                    .createAgentWeb()
                    .ready()
                    .go(setPageUrl() + "?deviceType=android")
            mAgentWeb!!.jsInterfaceHolder.addJavaObject("android", AndroidInterfaceForJS(this, mAgentWeb!!, titleWrap!!))
            mAgentWeb!!.agentWebSettings.webSettings.javaScriptEnabled = true
            mAgentWeb!!.agentWebSettings.webSettings.domStorageEnabled = true
            mAgentWeb!!.agentWebSettings.webSettings.loadsImagesAutomatically = true // 支持自动加载图片
            // mAgentWeb!!.agentWebSettings.webSettings.useWideViewPort = true // 设置webview推荐使用的窗口，使html界面自适应屏幕
            mAgentWeb!!.agentWebSettings.webSettings.setAppCacheEnabled(true) // 设置APP可以缓存
            mAgentWeb!!.agentWebSettings.webSettings.domStorageEnabled = true // 返回上个界面不刷新  允许本地缓存
            // mAgentWeb!!.agentWebSettings.webSettings.allowFileAccess = true // 设置可以访问文件
        }
        initPageData(view)
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
    open fun initPageData(fragmentView: View) {}

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
            if (view!!.canGoBack()) {
                tWrap!!.findViewById<ImageView>(R.id.pageBack).visibility = View.VISIBLE
            } else {
                tWrap!!.findViewById<ImageView>(R.id.pageBack).visibility = View.GONE
            }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            val title = view!!.title
            tWrap!!.findViewById<TextView>(R.id.pageTitle).text = title
        }
    }
    class MWebChromeClient(titleWrap: View?, loadingGroup: GifImageView?, webViewGroup: ViewGroup?) : WebChromeClient() {
        private var tWrap: View ? = null
        private var lGroup: GifImageView ? = null
        private var wGroup: ViewGroup ? = null

        init {
            tWrap = titleWrap
            lGroup = loadingGroup
            wGroup = webViewGroup
        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            if (view!!.canGoBack()) {
                tWrap!!.findViewById<ImageView>(R.id.pageBack).visibility = View.VISIBLE
            } else {
                tWrap!!.findViewById<ImageView>(R.id.pageBack).visibility = View.GONE
            }
        }

        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            tWrap!!.findViewById<TextView>(R.id.pageTitle).text = title
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("-=-=-=-=--=>> ", "$requestCode - $resultCode - $data")
        var pickerPath: String ? = null
        var pickerUri: Uri ? = null
        when (requestCode) {
            PICKER_PHOTO -> {
                if (resultCode == RESULT_OK) {
                    pickerUri = imageUri
                }
            }
            PICKER_PIC -> {
                if (resultCode == RESULT_OK) {
                    pickerPath = if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data)
                    } else {
                        handleImageBeforeKitKat(data)
                    }
                    pickerUri = Uri.fromFile(File(pickerPath))
                }
            }
        }
        if (pickerUri !== null) {
            var destinationUri: Uri?
            val destinationImage = File(this.context!!.externalCacheDir, "destination_image.jpg")
            if (destinationImage.exists()) {
                destinationImage.delete()
            }
            destinationImage.createNewFile()
            destinationUri = if (Build.VERSION.SDK_INT >= 24) {
                FileProvider.getUriForFile(this.context!!, MyApplication.instance!!.applicationContext.packageName + ".provider", destinationImage)
            } else {
                Uri.fromFile(destinationImage)
            }

            UCrop.of(pickerUri!!, destinationUri).withAspectRatio(1f, 1f).start(this.activity!!)
        }
    }

    @TargetApi(19)
    private fun handleImageOnKitKat (data: Intent?) : String? {
        var imagePath: String ? = null
        val uri: Uri = data!!.data
        if (DocumentsContract.isDocumentUri(this.context, uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri.authority) {
                val id: String = docId.split(":")[1]
                val selection: String = MediaStore.Images.Media._ID + "=" + id
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                val contentUri: Uri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), docId.toLong())
                imagePath = getImagePath(contentUri, null)
            }
        } else if ("content" == uri.scheme.toLowerCase()) {
            imagePath = getImagePath(uri, null)
        } else if ("file" == uri.scheme.toLowerCase()) {
            imagePath = uri.path
        }
        return imagePath
    }

    private fun handleImageBeforeKitKat (data: Intent?) : String? {
        val uri: Uri = data!!.data
        return getImagePath(uri, null)
    }

    private fun getImagePath (uri: Uri, selection: String?) : String? {
        var path: String ? = null
        val cursor: Cursor = this.context!!.contentResolver.query(uri, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path
    }
}