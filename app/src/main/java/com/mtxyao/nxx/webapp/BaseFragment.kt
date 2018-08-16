package com.mtxyao.nxx.webapp

import android.annotation.TargetApi
import android.app.Activity.RESULT_OK
import android.content.ContentUris
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import com.google.gson.Gson
import com.just.agentweb.*
import com.mtxyao.nxx.webapp.entity.UserData
import com.mtxyao.nxx.webapp.util.*
import com.yalantis.ucrop.UCrop
import java.io.File
import java.net.URI

abstract class BaseFragment(webView: Boolean) : Fragment() {
    private var initWebView: Boolean = false
    var mAgentWeb: AgentWeb? = null
    private var titleWrap: View ? = null
    private var statusBar: View ? = null
    private var pageSwipeRefresh: SwipeRefreshLayout ? = null
    companion object {
        var PICKER_PIC: Int = 1
        var PICKER_PHOTO: Int = 2
        var REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE: Int = 3
        var imageUri: Uri ? = null
    }

    init {
        initWebView = webView
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = getFragmentView(inflater, container)
        if (initWebView) {
            titleWrap = view.findViewById(R.id.titleBar)
            titleWrap!!.findViewById<ImageView>(R.id.pageBack).setOnClickListener {
                mAgentWeb!!.back()
                if (!mAgentWeb!!.webCreator.webView.canGoBack()) {
                    it.visibility = View.GONE
                }
            }
            statusBar = view.findViewById(R.id.statusBar)
            statusBar!!.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ComFun.getStateBarHeight())
            val pageOpt = getPageOpt()
            if (pageOpt.statusDark) {
                this.activity!!.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                this.activity!!.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }

            if (!pageOpt.showTitleBar) {
                statusBar!!.visibility = View.GONE
                titleWrap!!.visibility = View.GONE
            }
            pageSwipeRefresh = view.findViewById(R.id.pageSwipeRefresh)
            if (!pageOpt.canRef) {
                pageSwipeRefresh!!.isEnabled = false
            } else {
                pageSwipeRefresh!!.isEnabled = false
            }
            val webViewGroup: ViewGroup = view.findViewById(R.id.webAppMain)
            var pageUrl = Urls.WEB_BEFORE
            if (setPageUrl().indexOf(".html") > 0) {
                pageUrl += "/${setPageUrl()}"
            } else {
                if (setPageUrl() != "") {
                    pageUrl += "#/${setPageUrl()}"
                } else {
                    pageUrl += "#/app-no-page"
                    this.activity!!.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                    statusBar!!.visibility = View.GONE
                    titleWrap!!.visibility = View.GONE
                }
            }
            pageUrl += "?deviceType=android"
            mAgentWeb = AgentWeb.with(this)
                    .setAgentWebParent(webViewGroup, LinearLayout.LayoutParams(-1, -1))
                    .useDefaultIndicator()
                    .setAgentWebWebSettings(AbsAgentWebSettings.getInstance())
                    .setWebChromeClient(MWebChromeClient(titleWrap!!, webViewGroup))
                    .setWebViewClient(MWebViewClient(titleWrap!!, webViewGroup))
                    .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                    .setAgentWebUIController(AgentWebUIControllerImplBase())
                    .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)
                    .interceptUnkownUrl()
                    .createAgentWeb()
                    .ready()
                    .go(pageUrl)
            mAgentWeb!!.jsInterfaceHolder.addJavaObject("android", AndroidInterfaceForJS(this, mAgentWeb!!, titleWrap!!))
            mAgentWeb!!.agentWebSettings.webSettings.javaScriptEnabled = true
            mAgentWeb!!.agentWebSettings.webSettings.domStorageEnabled = true
            mAgentWeb!!.agentWebSettings.webSettings.loadsImagesAutomatically = true // 支持自动加载图片
            // mAgentWeb!!.agentWebSettings.webSettings.useWideViewPort = true // 设置webview推荐使用的窗口，使html界面自适应屏幕
            mAgentWeb!!.agentWebSettings.webSettings.setAppCacheEnabled(true) // 设置APP可以缓存
            mAgentWeb!!.agentWebSettings.webSettings.domStorageEnabled = true // 返回上个界面不刷新  允许本地缓存
            // mAgentWeb!!.agentWebSettings.webSettings.allowFileAccess = true // 设置可以访问文件
            if (Build.VERSION.SDK_INT >= 19) {
                mAgentWeb!!.agentWebSettings.webSettings.useWideViewPort = true
                mAgentWeb!!.agentWebSettings.webSettings.loadWithOverviewMode = true
            } else {
                mAgentWeb!!.agentWebSettings.webSettings.setSupportZoom(false)
                mAgentWeb!!.agentWebSettings.webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            }
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

    /**
     * 返回fragment页面view
     */
    abstract fun getFragmentView(inflater: LayoutInflater, container: ViewGroup?): View

    /**
     * 获取fragment页面webView相关配置
     */
    open fun getPageOpt(): PageOpt {
        return PageOpt()
    }

    /**
     * 设置fragment页面webView地址
     */
    open fun setPageUrl(): String {
        return ""
    }

    /**
     * 初始化fragment页面数据
     */
    open fun initPageData(fragmentView: View) {}

    /**
     * 获取到选取的相册图片或拍照图片
     */
    open fun getPickerImage(bitmap: Bitmap?, pickerUri: Uri?) {}

    /**
     * 获取到裁剪后的图片
     */
    open fun getCropImage(bitmap: Bitmap?, cropUri: Uri?, cropFile: File?) {}

    class MWebViewClient(titleWrap: View?, webViewGroup: ViewGroup?) : WebViewClient() {
        private var tWrap: View ? = null
        private var wGroup: ViewGroup ? = null

        init {
            tWrap = titleWrap
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
    class MWebChromeClient(titleWrap: View?, webViewGroup: ViewGroup?) : WebChromeClient() {
        private var tWrap: View ? = null
        private var wGroup: ViewGroup ? = null

        init {
            tWrap = titleWrap
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
        var pickerPath: String ?
        var bitmap: Bitmap ? = null
        var pickerUri: Uri ? = null
        when (requestCode) {
            PICKER_PHOTO -> {
                if (resultCode == RESULT_OK) {
                    pickerUri = imageUri
                    bitmap = BitmapFactory.decodeStream(this.context!!.contentResolver.openInputStream(imageUri))
                }
                getPickerImage(bitmap, pickerUri)
                if (pickerUri !== null) {
                    cropRawPhoto(pickerUri)
                }
            }
            PICKER_PIC -> {
                if (resultCode == RESULT_OK) {
                    pickerPath = if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data)
                    } else {
                        handleImageBeforeKitKat(data)
                    }
                    if (pickerPath != null) {
                        bitmap = BitmapFactory.decodeFile(pickerPath)
                        pickerUri = Uri.fromFile(File(pickerPath))
                    }
                }
                getPickerImage(bitmap, pickerUri)
                if (pickerUri !== null) {
                    cropRawPhoto(pickerUri)
                }
            }
            UCrop.REQUEST_CROP -> {
                if (resultCode == RESULT_OK) {
                    val cropOutUri: Uri ? = UCrop.getOutput(data!!)
                    bitmap = BitmapFactory.decodeStream(this.context!!.contentResolver.openInputStream(cropOutUri))
                    getCropImage(bitmap, cropOutUri, File(URI(cropOutUri.toString())))
                }
            }
            UCrop.RESULT_ERROR -> {
                ComFun.showToast(this.context!!, "裁剪失败", Toast.LENGTH_LONG)
            }
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
        if (cursor.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        }
        cursor.close()
        return path
    }

    private fun cropRawPhoto (uri: Uri) {
        val options: UCrop.Options = UCrop.Options()
        options.setToolbarColor(Color.parseColor("#004E96"))
        options.setToolbarTitle("裁剪图片")
        options.setStatusBarColor(Color.parseColor("#004E96"))
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG)
        options.setCompressionQuality(100)
        options.setFreeStyleCropEnabled(getPageOpt().freeStyleCropEnabled)
        UCrop.of(uri, Uri.fromFile(File(this.context!!.externalCacheDir, "destination_image.jpg")))
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(200, 200)
                .withOptions(options)
                .start(this.context!!, this)
    }

    open fun sendWebActivatedEvent () {
        if (mAgentWeb != null) {
            val userData: UserData? = UserDataUtil.getUserData(this.context!!)
            mAgentWeb!!.jsAccessEntrace.quickCallJs("androidEvent", "webActivated", Gson().toJson(userData))
        }
    }
}