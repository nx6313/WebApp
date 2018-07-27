package com.mtxyao.nxx.webapp

import android.annotation.TargetApi
import android.app.Activity
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
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.just.agentweb.AbsAgentWebSettings
import com.just.agentweb.AgentWeb
import com.just.agentweb.AgentWebUIControllerImplBase
import com.just.agentweb.DefaultWebClient
import com.mtxyao.nxx.webapp.util.AndroidInterfaceForJSActivity
import com.mtxyao.nxx.webapp.util.ComFun
import com.mtxyao.nxx.webapp.util.PageOpt
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_client_in.*
import java.io.File
import java.net.URI

abstract class BaseWebActivity : AppCompatActivity() {
    private var mAgentWeb: AgentWeb? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getActivityLayoutId())

        createAfter()

        statusBar.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ComFun.getStateBarHeight())

        val pageOpt = getPageOpt()
        if (pageOpt.statusDark) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }

        this.findViewById<ImageView>(R.id.pageBack).setOnClickListener {
            if (mAgentWeb!!.webCreator.webView.canGoBack()) {
                mAgentWeb!!.back()
            } else {
                finish()
            }
        }

        val webViewGroup: ViewGroup = this.findViewById(R.id.webAppMain)
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(webViewGroup, LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setAgentWebWebSettings(AbsAgentWebSettings.getInstance())
                .setWebChromeClient(MWebChromeClient(webViewGroup))
                .setWebViewClient(MWebViewClient(webViewGroup))
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .setAgentWebUIController(AgentWebUIControllerImplBase())
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)
                .interceptUnkownUrl()
                .createAgentWeb()
                .ready()
                .go(setPageUrl() + "?deviceType=android")
        mAgentWeb!!.jsInterfaceHolder.addJavaObject("android", AndroidInterfaceForJSActivity(this, mAgentWeb!!))
        mAgentWeb!!.agentWebSettings.webSettings.javaScriptEnabled = true
        mAgentWeb!!.agentWebSettings.webSettings.domStorageEnabled = true
        mAgentWeb!!.agentWebSettings.webSettings.loadsImagesAutomatically = true // 支持自动加载图片
        // mAgentWeb!!.agentWebSettings.webSettings.useWideViewPort = true // 设置webview推荐使用的窗口，使html界面自适应屏幕
        mAgentWeb!!.agentWebSettings.webSettings.setAppCacheEnabled(true) // 设置APP可以缓存
        mAgentWeb!!.agentWebSettings.webSettings.domStorageEnabled = true // 返回上个界面不刷新  允许本地缓存
        // mAgentWeb!!.agentWebSettings.webSettings.allowFileAccess = true // 设置可以访问文件
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when(keyCode) {
            KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_DOWN -> {
                if (mAgentWeb != null && mAgentWeb!!.handleKeyEvent(keyCode, event)) {
                    return true
                } else {
                    finish()
                }
            }
        }
        return true
    }

    /**
     * 返回activity页面layoutId
     */
    abstract fun getActivityLayoutId(): Int

    /**
     * 页面onCreate之后执行的回调
     */
    open fun createAfter() {}

    /**
     * 获取activity页面相关配置
     */
    open fun getPageOpt(): PageOpt {
        return PageOpt()
    }

    /**
     * 设置activity页面webView地址
     */
    open fun setPageUrl(): String {
        return ""
    }

    /**
     * 获取到选取的相册图片或拍照图片
     */
    open fun getPickerImage(bitmap: Bitmap?, pickerUri: Uri?) {}

    /**
     * 获取到裁剪后的图片
     */
    open fun getCropImage(bitmap: Bitmap?, cropUri: Uri?, cropFile: File?) {}

    class MWebViewClient(webViewGroup: ViewGroup?) : WebViewClient() {
        private var wGroup: ViewGroup ? = null

        init {
            wGroup = webViewGroup
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
        }
    }
    class MWebChromeClient(webViewGroup: ViewGroup?) : WebChromeClient() {
        private var wGroup: ViewGroup ? = null

        init {
            wGroup = webViewGroup
        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
        }

        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var pickerPath: String ?
        var bitmap: Bitmap ? = null
        var pickerUri: Uri? = null
        when (requestCode) {
            BaseFragment.PICKER_PHOTO -> {
                if (resultCode == Activity.RESULT_OK) {
                    pickerUri = BaseFragment.imageUri
                    bitmap = BitmapFactory.decodeStream(this.contentResolver.openInputStream(BaseFragment.imageUri))
                }
                getPickerImage(bitmap, pickerUri)
                if (pickerUri !== null) {
                    cropRawPhoto(pickerUri!!)
                }
            }
            BaseFragment.PICKER_PIC -> {
                if (resultCode == Activity.RESULT_OK) {
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
                    cropRawPhoto(pickerUri!!)
                }
            }
            UCrop.REQUEST_CROP -> {
                if (resultCode == Activity.RESULT_OK) {
                    val cropOutUri: Uri? = UCrop.getOutput(data!!)
                    bitmap = BitmapFactory.decodeStream(this.contentResolver.openInputStream(cropOutUri))
                    getCropImage(bitmap, cropOutUri, File(URI(cropOutUri.toString())))
                }
            }
            UCrop.RESULT_ERROR -> {
                ComFun.showToast(this, "裁剪失败", Toast.LENGTH_LONG)
            }
        }
    }

    @TargetApi(19)
    private fun handleImageOnKitKat (data: Intent?) : String? {
        var imagePath: String ? = null
        val uri: Uri = data!!.data
        if (DocumentsContract.isDocumentUri(this, uri)) {
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
        val cursor: Cursor = this.contentResolver.query(uri, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path
    }

    private fun cropRawPhoto (uri: Uri) {
        val options: UCrop.Options = UCrop.Options()
        options.setToolbarColor(Color.parseColor("#004E96"))
        options.setToolbarTitle("裁剪图片")
        options.setStatusBarColor(Color.parseColor("#004E96"))
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG)
        options.setCompressionQuality(100)
        options.setFreeStyleCropEnabled(false)
        UCrop.of(uri, Uri.fromFile(File(this.externalCacheDir, "destination_image.jpg")))
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(200, 200)
                .withOptions(options)
                .start(this)
    }
}