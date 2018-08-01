package com.mtxyao.nxx.webapp

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import com.just.agentweb.AbsAgentWebSettings
import com.just.agentweb.AgentWeb
import com.just.agentweb.AgentWebUIControllerImplBase
import com.just.agentweb.DefaultWebClient
import com.mtxyao.nxx.webapp.util.AndroidBug5497Workaround
import com.mtxyao.nxx.webapp.util.AndroidInterfaceForJSActivity
import com.mtxyao.nxx.webapp.util.ComFun
import com.mtxyao.nxx.webapp.util.PageOpt
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_client_in.*
import java.io.File
import java.io.Serializable
import java.net.URI

abstract class BaseWebActivity : AppCompatActivity() {
    var mAgentWeb: AgentWeb? = null
    companion object {
        open var baseWebHandler: Handler ? = null
        open var PICKER_PIC: Int = 1
        open var PICKER_PHOTO: Int = 2
        open var REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE: Int = 3
        open var REQUEST_CODE_ASK_CALL_PHONE: Int = 4
        open var imageUri: Uri ? = null
        open var MSG_SELECT_IMAGE: Int = 5
        open var webEventName: String ? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getActivityLayoutId())

        AndroidBug5497Workaround.assistActivity(this, true)

        createAfter()

        statusBar.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ComFun.getStateBarHeight())

        val pageOpt = getPageOpt()
        if (pageOpt.statusDark) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
        if (pageOpt.titleBarTransparency) {
            this.findViewById<View>(R.id.statusBar).setBackgroundColor(Color.TRANSPARENT)
            this.findViewById<RelativeLayout>(R.id.titleBar).setBackgroundColor(Color.TRANSPARENT)
        }
        if (pageOpt.titleBarHighlight) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            this.findViewById<ImageView>(R.id.pageBack).setImageResource(R.drawable.back)
            this.findViewById<TextView>(R.id.appTitle).setTextColor(Color.parseColor("#FFFFFF"))
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            this.findViewById<ImageView>(R.id.pageBack).setImageResource(R.drawable.back_dark)
            this.findViewById<TextView>(R.id.appTitle).setTextColor(Color.parseColor("#212121"))
        }
        if (pageOpt.webViewFull) {
            val webWrapLayout = this.findViewById<SmartRefreshLayout>(R.id.smartRefreshLayout)
            val ls = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
            ls.bottomToBottom = ConstraintSet.PARENT_ID
            ls.topToBottom = ConstraintSet.PARENT_ID
            webWrapLayout.layoutParams = ls
        }
        if (pageOpt.titleBarColor != "") {
            this.findViewById<View>(R.id.statusBar).setBackgroundColor(Color.parseColor(pageOpt.titleBarColor))
            this.findViewById<RelativeLayout>(R.id.titleBar).setBackgroundColor(Color.parseColor(pageOpt.titleBarColor))
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

        baseWebHandler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                when (msg!!.what) {
                    MSG_SELECT_IMAGE -> {
                        val list: MutableList<Uri> = (msg!!.data.getSerializable("baseWebSerializable") as BaseWebSerializable).list
                        val bitmaps = mutableListOf<Bitmap>()
                        val uris = mutableListOf<Uri>()
                        val files = mutableListOf<File>()
                        for (u in list) {
                            bitmaps.add(BitmapFactory.decodeStream(this@BaseWebActivity.contentResolver.openInputStream(u)))
                            uris.add(u)
                            files.add(File(URI(u.toString())))
                        }
                        webEventName = msg!!.data.getString("webEventName")
                        getLatelyImage(bitmaps, uris, files, webEventName)
                    }
                }
            }
        }
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
    open fun getPickerImage(bitmap: Bitmap?, pickerUri: Uri?, pickerFile: File?) {}

    /**
     * 获取到裁剪后的图片
     */
    open fun getCropImage(bitmap: Bitmap?, cropUri: Uri?, cropFile: File?, webEventName: String?) {}

    /**
     * 获取到选择到的最近的图片
     */
    open fun getLatelyImage(bitmaps: MutableList<Bitmap>?, latelyUris: MutableList<Uri>?, latelyFiles: MutableList<File>?, webEventName: String?) {}

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

        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
            Log.i("WEB ## console --->>> ", consoleMessage!!.message())
            return super.onConsoleMessage(consoleMessage)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(Intent.ACTION_GET_CONTENT, null)
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                    startActivityForResult(intent, PICKER_PIC)
                } else {
                    ComFun.showToast(this, "您拒绝了选取图片的权限", Toast.LENGTH_SHORT)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var pickerPath: String ?
        var bitmap: Bitmap ? = null
        var pickerUri: Uri? = null
        when (requestCode) {
            PICKER_PHOTO -> {
                if (resultCode == Activity.RESULT_OK) {
                    pickerUri = imageUri
                    bitmap = BitmapFactory.decodeStream(this.contentResolver.openInputStream(imageUri))
                }
                getPickerImage(bitmap, pickerUri, File(URI(pickerUri.toString())))
                if (pickerUri !== null) {
                    cropRawPhoto(pickerUri!!)
                }
            }
            PICKER_PIC -> {
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
                getPickerImage(bitmap, pickerUri, File(URI(pickerUri.toString())))
                if (pickerUri !== null) {
                    cropRawPhoto(pickerUri!!)
                }
            }
            UCrop.REQUEST_CROP -> {
                if (resultCode == Activity.RESULT_OK) {
                    val cropOutUri: Uri? = UCrop.getOutput(data!!)
                    bitmap = BitmapFactory.decodeStream(this.contentResolver.openInputStream(cropOutUri))
                    getCropImage(bitmap, cropOutUri, File(URI(cropOutUri.toString())), webEventName)
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
        options.setFreeStyleCropEnabled(getPageOpt().freeStyleCropEnabled)
        UCrop.of(uri, Uri.fromFile(File(this.externalCacheDir, "destination_image.jpg")))
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(200, 200)
                .withOptions(options)
                .start(this)
    }

    class BaseWebSerializable(selectUriList: MutableList<Uri>) : Serializable {
        open var list: MutableList<Uri> = selectUriList
    }
}